package com.saravyasystems.filminex.identity.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.saravyasystems.filminex.identity.api.IdentityService;
import com.saravyasystems.filminex.identity.api.UserIdentity;
import com.saravyasystems.filminex.identity.api.Workspace;
import com.saravyasystems.filminex.identity.api.WorkspaceAccessDeniedException;
import com.saravyasystems.filminex.identity.api.WorkspaceMembership;
import com.saravyasystems.filminex.identity.api.WorkspaceRole;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.simple.JdbcClient;

@SpringBootTest(properties = {
    "spring.task.scheduling.enabled=false",
    "filminex.events.outbox.poll-interval=1h"
})
class IdentityIntegrationTests {

    @Autowired
    private IdentityService identities;

    @Autowired
    private JdbcClient jdbcClient;

    @BeforeEach
    void prepareDatabase() {
        jdbcClient.sql("delete from filminex.event_outbox").update();
        jdbcClient.sql("delete from filminex.project").update();
        jdbcClient.sql("delete from filminex.workspace_membership").update();
        jdbcClient.sql("delete from filminex.workspace").update();
        jdbcClient.sql("delete from filminex.filminex_user").update();
    }

    @Test
    void createsWorkspaceWithOwnerAsAdminAndDurableEvent() {
        UserIdentity owner = identities.registerUser("Owner@Filminex.test", "Owner");

        Workspace workspace = identities.createWorkspace(owner.id(), "First Production");

        assertThat(owner.email()).isEqualTo("owner@filminex.test");
        assertThat(identities.findMembership(workspace.id(), owner.id()))
                .get()
                .extracting(WorkspaceMembership::role)
                .isEqualTo(WorkspaceRole.ADMIN);
        assertThat(eventCount(workspace.id(), "identity.workspace-created.v1")).isOne();
    }

    @Test
    void adminCanManageRolesAndEveryMemberCanListTheWorkspaceDirectory() {
        UserIdentity owner = user("owner");
        UserIdentity member = user("member");
        Workspace workspace = identities.createWorkspace(owner.id(), "Role Test");

        WorkspaceMembership added =
                identities.addMember(workspace.id(), owner.id(), member.id(), WorkspaceRole.VIEWER);
        WorkspaceMembership changed =
                identities.changeRole(workspace.id(), owner.id(), member.id(), WorkspaceRole.EDITOR);

        assertThat(added.role()).isEqualTo(WorkspaceRole.VIEWER);
        assertThat(changed.role()).isEqualTo(WorkspaceRole.EDITOR);
        assertThat(identities.listMemberships(workspace.id(), member.id())).hasSize(2);
        assertThat(eventCount(workspace.id(), "identity.member-added.v1")).isOne();
        assertThat(eventCount(workspace.id(), "identity.member-role-changed.v1")).isOne();
    }

    @Test
    void nonAdminCannotChangeWorkspaceMembership() {
        UserIdentity owner = user("owner");
        UserIdentity editor = user("editor");
        UserIdentity candidate = user("candidate");
        Workspace workspace = identities.createWorkspace(owner.id(), "Protected");
        identities.addMember(workspace.id(), owner.id(), editor.id(), WorkspaceRole.EDITOR);

        assertThatThrownBy(() -> identities.addMember(
                        workspace.id(), editor.id(), candidate.id(), WorkspaceRole.VIEWER))
                .isInstanceOf(WorkspaceAccessDeniedException.class);
        assertThat(identities.findMembership(workspace.id(), candidate.id())).isEmpty();
    }

    @Test
    void workspaceMustRetainAtLeastOneAdmin() {
        UserIdentity owner = user("owner");
        Workspace workspace = identities.createWorkspace(owner.id(), "Admin Safety");

        assertThatThrownBy(() -> identities.changeRole(
                        workspace.id(), owner.id(), owner.id(), WorkspaceRole.EDITOR))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("at least one ADMIN");
        assertThatThrownBy(() -> identities.removeMember(workspace.id(), owner.id(), owner.id()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("at least one ADMIN");
    }

    @Test
    void membershipsRemainIsolatedByWorkspace() {
        UserIdentity firstOwner = user("first-owner");
        UserIdentity secondOwner = user("second-owner");
        UserIdentity member = user("member");
        Workspace first = identities.createWorkspace(firstOwner.id(), "First");
        Workspace second = identities.createWorkspace(secondOwner.id(), "Second");
        identities.addMember(first.id(), firstOwner.id(), member.id(), WorkspaceRole.VIEWER);

        assertThat(identities.findMembership(first.id(), member.id())).isPresent();
        assertThat(identities.findMembership(second.id(), member.id())).isEmpty();
        assertThatThrownBy(() -> identities.listMemberships(second.id(), member.id()))
                .isInstanceOf(WorkspaceAccessDeniedException.class);
    }

    @Test
    void emailIdentityIsCaseInsensitive() {
        identities.registerUser("Person@Filminex.test", "First");

        assertThatThrownBy(
                        () -> identities.registerUser("person@filminex.test", "Duplicate"))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    private UserIdentity user(String name) {
        return identities.registerUser(name + "@filminex.test", name);
    }

    private long eventCount(UUID workspaceId, String eventType) {
        return jdbcClient.sql("""
                        select count(*) from filminex.event_outbox
                        where workspace_id = :workspaceId and event_type = :eventType
                        """)
                .param("workspaceId", workspaceId)
                .param("eventType", eventType)
                .query(Long.class)
                .single();
    }
}
