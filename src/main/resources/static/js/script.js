let currentTheme = getTheme();

document.addEventListener("DOMContentLoaded", () => {
    changeTheme();
});

// Function to toggle theme
function toggleTheme() {
    const oldTheme = currentTheme;
    currentTheme = currentTheme === "dark" ? "light" : "dark";
    setTheme(currentTheme);

    // Remove the old theme
    document.querySelector('html').classList.remove(oldTheme);
    // Change the theme name
    document.getElementById("theme_change_button").querySelector("span").textContent = currentTheme === "dark" ? "Light" : "Dark";
    // Add the current new theme
    document.querySelector('html').classList.add(currentTheme);
}

// Function to handle theme change
function changeTheme() {
    // Set theme class to html element
    document.querySelector('html').classList.add(currentTheme);

    // Change the theme name
    document.getElementById("theme_change_button").querySelector("span").textContent = currentTheme === "dark" ? "Light" : "Dark";

    // Set listener to change theme using button
    document.querySelector('#theme_change_button').addEventListener("click", toggleTheme);
}

// Function to set theme in localStorage
function setTheme(theme) {
    localStorage.setItem("theme", theme);
}

// Function to get theme from localStorage
function getTheme() {
    return localStorage.getItem("theme") || "light";
}
