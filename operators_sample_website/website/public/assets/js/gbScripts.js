$('.terminals').on('click',function(){

    $("input[name='sell']").removeClass('btn-secondary');
    $("input[name='sell']").addClass('btn-primary');
    $('.terminals').removeClass('border-primary');
    $(this).addClass('border-primary');
    let serialNumber = $(this).attr('id');
    $('input[name="selectedTerminal"]').val(serialNumber);
});

$("input[name='amount']").on('keyup', function(){
    $("input[name='sell']").removeClass('btn-secondary');
    $("input[name='sell']").addClass('btn-primary');
});

