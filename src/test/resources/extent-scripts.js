
document.addEventListener("DOMContentLoaded", function() {
    const headers = document.querySelectorAll(".test-case h3");
    headers.forEach(header => {
        header.style.cursor = "pointer";
        const content = header.nextElementSibling;
        if (content) {
            content.style.display = "none";
            header.addEventListener("click", () => {
                const visible = content.style.display === "block";
                content.style.display = visible ? "none" : "block";
            });
        }
    });
});
