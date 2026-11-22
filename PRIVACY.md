# Diff AI Studio — Privacy Policy

**Draft for release review.** Replace the contact placeholder and verify this policy against the final app build before publishing.

## Data processed
Diff AI Studio is designed for on-device image generation. The app may store these items in app-private storage:
- Imported AI model files
- Prompts and negative prompts
- Generated images and generation settings (resolution, steps, CFG, seed, sampler/scheduler)
- Image-to-image source images and inpainting masks
- User-created prompt presets and favorites

## Local processing
The image-generation workflow is intended to run locally on the device using the installed native inference engine. Prompts, source images, masks, and generated images are not intentionally uploaded by the local generation workflow.

## Network access
Internet access is used for model downloads and other explicitly requested download functions. The final release must disclose any additional network services introduced before release.

## Export and sharing
Users may export generated images to shared Pictures storage or use Android's share sheet. Those actions are initiated by the user and may make the selected image available to the chosen destination or app.

## Retention and deletion
App-private models, prompt presets, source images, masks, and generation history remain on the device until users delete them or uninstall the app. The app's history controls allow deleting saved generated images. Exported or shared copies are controlled by the user and recipient apps.

## Children
The final publisher should determine and document the appropriate age rating and child-directed status before release.

## Third-party model licenses
Downloaded or imported models may have their own license, usage restrictions, attribution requirements, or commercial-use limitations. Users are responsible for selecting and using models in compliance with their terms. The release should include links to model license information where applicable.

## Contact
Privacy questions: **[Publisher contact email — fill before release]**

## Release review
This draft must be reviewed against the actual release build, network endpoints, permissions, third-party SDKs, and Play Store requirements before publication.
