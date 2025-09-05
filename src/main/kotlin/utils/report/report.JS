/**
 * Toggles any class on any given element.
 * @param id the id of the target element.
 * @param className the class name to be toggled.
 */
function toggle(id, className) {
    const content = document.getElementById(id);
    content.classList.toggle(className);
}

/**
 * Toggles the 'dark' class on the body and persists the mode.
 */
function toggleDarkMode() {
    const isDark = document.body.classList.toggle('dark');
    localStorage.setItem('darkMode', isDark ? 'enabled' : 'disabled');
}

/**
 * Applies the persisted dark mode state on page load.
 */
document.addEventListener('DOMContentLoaded', function () {
    if (localStorage.getItem('darkMode') === 'enabled') {
        document.body.classList.add('dark');
    }
});

/**
 * Animates a card to open smoothly.
 * @param id of the card content.
 */
function toggleCard(id) {
    const body = document.getElementById(id);
    const isOpen = body.classList.contains('show');

    if (isOpen) {
        // collapse
        requestAnimationFrame(() => {
            body.style.maxHeight = '0';
            body.classList.remove('show');
        });
    } else {
        // expand
        body.classList.add('show');
        requestAnimationFrame(() => {
            body.style.maxHeight = `${body.scrollHeight}px`;
        });
    }
}