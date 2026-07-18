# Chapter 9 — Identity, Sharing, and Capabilities

## Identity and tenancy

A workspace is the ownership and collaboration boundary. Users act through memberships and roles. Every protected operation is authorized against both the workspace and target entity.

## Access modes

- **Read mode:** experience the complete story and inspect allowed production knowledge.
- **Write mode:** create or refine authorized content.
- **Administrative control:** manage membership, sharing, entitlements, and workspace policy.

Specific role names and permission matrices are implementation artifacts to be defined by schema and API work.

## Sharing

Sharing records owner, recipient, access mode, scope, and lifecycle. A shared copy or promotion never obscures origin or ownership. Permission changes are logged.

## Capability Engine

Capabilities determine what can be used and therefore what should be visible. Inputs include:

- subscription and add-ons;
- workspace policy;
- user permission;
- project production mode;
- entity context;
- provider availability.

The UI and API enforce the same decisions. Hiding a control is not authorization.

## Commercial boundary

Users may subscribe without AI or animation. AI Studio is an add-on capability. Module development must respect pricing filters even before billing implementation is complete.

Authentication, pricing, billing-provider selection, plan amounts, and detailed entitlements are delivery decisions, not declarations invented by Genesis.
