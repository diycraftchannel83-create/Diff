# Offline Models for Diff AI Studio

> **Important:** The current app has model import/management UI, but native inference/model loading is not wired yet. Downloading/importing a model now prepares the file; image generation will work after the inference integration is completed.

## Recommended starter model (stable-diffusion.cpp / GGUF)

- **Stable Diffusion 1.5 Q8 GGUF (~1.9 GB)**: [Download model package from Hugging Face](https://huggingface.co/cuesetac/sd15-mediapipe/resolve/main/sd15-sdcpp.zip)
- Model source and license details: [Hugging Face model card](https://huggingface.co/cuesetac/sd15-mediapipe)
- The package contains `sd15-q8c16.gguf`, prepared for stable-diffusion.cpp. The reported package is about 1.9 GB; actual download size and free space requirements may vary.

## Alternative checkpoint (large, not recommended for low-memory phones)

- **Stable Diffusion 1.5 original safetensors**: [Download v1-5-pruned-emaonly.safetensors](https://huggingface.co/stable-diffusion-v1-5/stable-diffusion-v1-5/resolve/main/v1-5-pruned-emaonly.safetensors)
- [Model repository and license](https://huggingface.co/stable-diffusion-v1-5/stable-diffusion-v1-5)

## Download / import steps

1. Open the download link in a browser and download the file.
2. If downloading the ZIP package, extract it and locate the `.gguf` model file.
3. In Diff AI Studio, open **Models / Model Manager** and use **Import model** to select the model file.
4. Check free storage first. Keep several GB free for model storage and generated images.
5. Model compatibility and generation are not guaranteed until the app's native inference integration is completed.

## Model licenses and safety

Read the license and model card for each model before use or redistribution. These links point to third-party model hosts; Diff AI Studio does not own or redistribute the model weights.
