function refresh() {
    $.get('/messages', function (msg) {
        var list = '';
        (msg || []).forEach(function (msg) {
            list = list
                + '<tr>'
                + '<td>' + msg.id + '</td>'
                + '<td>' + msg.topic + '</td>'
                + '<td>' + msg.payload + '</td>'
                + '</tr>'
        });
        if (list.length > 0) {
            list = ''
                + '<table><thead><th>Id</th><th>Topic</th><th>Payload</th></thead>'
                + list
                + '</table>';
        } else {
            list = "No messages in database"
        }
        $('#all-messages').html(list);
    });
}


$(document).ready(function () {
    refresh();
});