function startAjax(transactionId) {
    console.log(transactionId);
    $.ajax({
        url: "/status",
        type: 'GET',
        data: {transaction_id: transactionId},
        success: function (data) {
            showMessage(data);
        }
    });
}

function showMessage(msg) {
    if ($.isEmptyObject(msg.error)) {
        $('#status').html(msg.success);
    } else {
        console.log('Houston we have problem');
    }
}
