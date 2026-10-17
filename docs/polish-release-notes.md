# Development polishing notes

Player changes: clearer mission contexts, tablet panels, phone and large-text layouts, kitchen scoop totals, turned arrays, hint scrolling, personalised revision, adaptive/themed icons and improved button feedback. Explicit pauses survive backgrounding; repeated completion cannot award twice; damaged missions fail safely without discarding valid learning records.

Parent changes: automatic relocking, fresh authorisation checks at protected actions, return-to-adventure control, clearer store/pending/restore status and an isolated US English debug preview. No extra language is advertised as approved.

Engineering changes: Android host/native-layout tests, actual SDK response-model adapter tests, source/translation review hashes, bounded non-redirecting translation transport, approved-pack staging, private local signing configuration, pinned CI/build tooling and executable verification/audit commands. Website configuration and URL/server behaviour now have tests.

These are development notes, not final Appstore claims. Speech, device, purchasing and language claims must reflect actual release evidence.
