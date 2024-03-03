

function describe(subjectNameHolderId, resultHolderId) {
    const subjectName = document.getElementById(subjectNameHolderId).value;
    const url = `/api/describe?subject=${encodeURIComponent(subjectName)}`;

    fetch(url)
        .then(response => response.text()) // Assuming the response is plain text
        .then(data => {
            document.getElementById(resultHolderId).innerText = data;
        })
        .catch(error => console.error('Error:', error));
}