document.addEventListener('DOMContentLoaded', () => {
    const imageInput = document.getElementById('imageInput');
    const uploadButton = document.getElementById('uploadButton');
    const uploadMessage = document.getElementById('uploadMessage');
    const imageGallery = document.getElementById('imageGallery');
    const galleryMessage = document.getElementById('galleryMessage');

    // Modal elements
    const imageModal = document.getElementById('imageModal');
    const modalImage = document.getElementById('modalImage');
    const closeButton = document.getElementsByClassName('close')[0];

    // Backend endpoints (ensure these match your Spring Boot Controller)
    const UPLOAD_URL = '/home/uploadFile'; // Upload endpoint
    const LIST_IMAGES_URL = '/home/images'; // List images endpoint (updated to /home/images)

    // Function to fetch and display images in the gallery
    const fetchAndDisplayImages = async () => {
        imageGallery.innerHTML = ''; // Clear existing images
        galleryMessage.textContent = 'Loading images...';
        try {
            const response = await fetch(LIST_IMAGES_URL);
            if (!response.ok) {
                // Agar 404 error aaye to gallery message update karein
                if (response.status === 404) {
                    galleryMessage.textContent = 'Image listing endpoint (/home/images) not found on server. Please ensure it is implemented in your Spring Boot backend.';
                } else {
                    throw new Error(`HTTP error! status: ${response.status}`);
                }
                return; // Stop further execution if endpoint not found
            }
            const images = await response.json();

            if (images.length === 0) {
                galleryMessage.textContent = 'No images uploaded yet.';
            } else {
                galleryMessage.textContent = ''; // Clear message if images are found
                images.forEach(imageUrl => {
                    const imgElement = document.createElement('img');
                    imgElement.src = imageUrl;
                    imgElement.alt = 'Uploaded Image';
                    imgElement.classList.add('gallery-thumbnail'); // Add a class for styling if needed

                    // Click listener for opening the modal
                    imgElement.addEventListener('click', () => {
                        modalImage.src = imageUrl; // Modal image source set to clicked image
                        imageModal.style.display = 'block'; // Show the modal
                    });

                    imageGallery.appendChild(imgElement);
                });
            }
        } catch (error) {
            console.error('Error fetching images:', error);
            galleryMessage.textContent = 'Failed to load images. Please check the server connection or S3 bucket configuration.';
        }
    };

    // Handle image upload
    uploadButton.addEventListener('click', async () => {
        const file = imageInput.files[0];
        if (!file) {
            uploadMessage.textContent = 'Please select an image to upload.';
            uploadMessage.style.color = 'orange';
            return;
        }

        uploadMessage.textContent = 'Uploading...';
        uploadMessage.style.color = 'blue';

        const formData = new FormData();
        formData.append('file', file); // 'file' should match the @RequestPart name in your Spring Boot controller

        try {
            const response = await fetch(UPLOAD_URL, {
                method: 'POST',
                body: formData,
            });

            if (response.ok) {
                const result = await response.text(); // Assuming backend returns a success message or URL
                uploadMessage.textContent = `Upload successful! ${result}`;
                uploadMessage.style.color = 'green';
                imageInput.value = ''; // Clear the file input
                fetchAndDisplayImages(); // Refresh the gallery after successful upload
            } else {
                const errorText = await response.text();
                throw new Error(`Upload failed: ${response.status} - ${errorText}`);
            }
        } catch (error) {
            console.error('Error uploading image:', error);
            uploadMessage.textContent = `Upload failed: ${error.message}`;
            uploadMessage.style.color = 'red';
        }
    });

    // Modal close functionality
    closeButton.addEventListener('click', () => {
        imageModal.style.display = 'none'; // Hide the modal
    });

    // Modal ko bahar click karne par band karein
    window.addEventListener('click', (event) => {
        if (event.target === imageModal) {
            imageModal.style.display = 'none'; // Hide the modal
        }
    });

    // Initial fetch of images when the page loads
    fetchAndDisplayImages();
});
