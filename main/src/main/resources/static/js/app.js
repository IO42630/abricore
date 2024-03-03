// app.js
function queryProperty() {
    const propertyName = document.getElementById('propertyNameInput').value;
    const url = `/query-property?propertyName=${encodeURIComponent(propertyName)}`;

    fetch(url)
        .then(response => response.text()) // Assuming the response is plain text
        .then(data => {
            document.getElementById('result').innerText = data;
        })
        .catch(error => console.error('Error:', error));
}
