# Third-Party Notices

This file is a release checklist, not a final legal notice.

## Native inference engine
- stable-diffusion.cpp: source is pinned in the Android CMake configuration. Verify the exact upstream license text and include the required notice in the release package.

## AI model files
- Model files are user-imported or downloaded. Model licenses vary; do not assume all checkpoints allow commercial use, redistribution, or unrestricted output use.
- Add model-specific attribution/license links in the model download screen and release documentation where required.

## Android libraries
Review the dependency graph from the final Gradle build and list each bundled third-party library, version, and required license notice before release.

## Release gate
- [ ] Verify exact pinned native engine license and include notice
- [ ] Audit final Gradle dependencies and transitive licenses
- [ ] Add model source/license information for every model distributed or promoted
- [ ] Confirm publisher contact and privacy policy are finalized
- [ ] Confirm privacy disclosures match actual network behavior and permissions
