const baseURL = "http://localhost:8081";

const viewContactModal = document.getElementById('view_contact_modal');

// options with default values
const options = {
    placement: 'bottom-right',
    backdrop: 'dynamic',
    backdropClasses:
        'bg-gray-900/50 dark:bg-gray-900/80 fixed inset-0 z-40',
    closable: true,
    onHide: () => {
        console.log('modal is hidden');
    },
    onShow: () => {
        console.log('modal is shown');
    },
    onToggle: () => {
        console.log('modal has been toggled');
    },
};

// instance options object
const instanceOptions = {
    id: 'view_contact_modal',
    override: true
};

const contactModal = new Modal(viewContactModal, options, instanceOptions);

function openContactModal() {
    contactModal.show();
}

function closeContactModal() {
    contactModal.hide();
}

async function loadContactData(id) {
    console.log(`Loading contact data for ID: ${id}`);

    try {
        const response = await fetch(`${baseURL}/api/contacts/${id}`);

        if (!response.ok) {
            throw new Error(`Failed to fetch data: ${response.statusText}`);
        }

        const data = await response.json();

        // Setting values on the contact_modal
        document.querySelector("#contact_name").innerHTML = data.name;
        document.querySelector("#contact_email").innerHTML = data.email;
        document.querySelector("#contact_image").src = data.picture;
        document.querySelector("#contact_address").innerHTML = data.address;
        document.querySelector("#contact_phone").innerHTML = data.phoneNumber;
        document.querySelector("#contact_about").innerHTML = data.description;
        const contactFavourite = document.querySelector("#contact_favourite");
        if (data.favourite) {
            contactFavourite.innerHTML = "<i class='fa-solid fa-star text-yellow-400'></i><i class='fa-solid fa-star text-yellow-400'></i><i class='fa-solid fa-star text-yellow-400'></i><i class='fa-solid fa-star text-yellow-400'></i><i class='fa-solid fa-star text-yellow-400'></i>";
        } else {
            contactFavourite.innerHTML = "Not a Favourite Contact";
        }
        document.querySelector("#contact_website").href = data.websiteLink;
        document.querySelector("#contact_website").innerHTML = data.websiteLink;
        document.querySelector("#contact_linkedIn").href = data.linkedInLink;
        document.querySelector("#contact_linkedIn").innerHTML = data.linkedInLink;
        openContactModal();
        console.log("Contact data:", data);

        return data;
    } catch (error) {
        console.error("Error loading contact data:", error);
    }
}


// Delete Contact
async function deleteContact(id) {
    Swal.fire({
        title: "Do you want to delete the contact?",
        icon: "warning",
        showCancelButton: true,
        confirmButtonText: "Delete",
        confirmButtonColor: "#6b5b95", // Set your desired purple color here
        cancelButtonColor: "#d33",     // Optional: Set a color for the cancel button
    }).then((result) => {
        if (result.isConfirmed) {
            const url = `${baseURL}/user/contacts/delete/${id}`;
            window.location.replace(url);
        }
    });
}

