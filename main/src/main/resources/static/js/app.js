const fromEvol = new Pikaday({
                                 field: document.getElementById('fromEvol'),
                                 format: 'DD-MM-yyyy',
                                 toString(date, format) {
                                     return moment(date).format(format);
                                 },
                                 parse(dateString, format) {
                                     return moment(dateString, format).toDate();
                                 }
});
const toEvol = new Pikaday({
                               field: document.getElementById('toEvol'),
                               format: 'DD-MM-yyyy',
                               toString(date, format) {
                                   return moment(date).format(format);
                               },
                               parse(dateString, format) {
                                   return moment(dateString, format).toDate();
                               }
});

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