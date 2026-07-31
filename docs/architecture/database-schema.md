# Database Schema

**Status:** Implemented Sprint 0 foundation

PostgreSQL schema `filminex` is changed only by Flyway. The foundation ownership map is:

| Migration | Owner | Records |
|---|---|---|
| V1 | projects | workspace and project foundation |
| V2 | audit/outbox | transactional event outbox |
| V3 | identity | users, workspaces, memberships |
| V4 | audit | append-only audit events and guards |
| V5 | capabilities | workspace entitlements |
| V6 | rights | local talent-rights grants |
| V7 | localization | tracks, cues, and voice profiles |

Foreign keys preserve ownership boundaries, workspace query paths are indexed, and
timestamps use time-zone-aware values. Migration history is validated at application
startup. Future modules add forward-only migrations; applied files are never edited.
