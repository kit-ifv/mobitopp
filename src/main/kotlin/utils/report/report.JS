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
 * Toggles the 'dark' class on the body.
 */
function toggleDarkMode() {
    document.body.classList.toggle('dark')
}

/**
 * Animates a card to open smoothly.
 * @param id of the card content.
 */
function toggleCard(id) {
    const body = document.getElementById(id);
    const isOpen = body.classList.contains('show');

    if (isOpen) {
        // collapse
        body.style.maxHeight = `${body.scrollHeight}px`;
        requestAnimationFrame(() => {
            body.style.maxHeight = '0';
            body.classList.remove('show');
        });
    } else {
        // expand
        body.classList.add('show');
        body.style.maxHeight = '0';
        requestAnimationFrame(() => {
            body.style.maxHeight = `${body.scrollHeight}px`;
        });
    }
}