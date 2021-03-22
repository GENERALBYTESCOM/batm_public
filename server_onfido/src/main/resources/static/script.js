const urlParams = new URLSearchParams(window.location.search)
const applicantId = urlParams.get('a')

function initOnfido(token) {

    Onfido.init({
        useModal: false,
        token: token,
        containerId: 'onfido-mount',
        onComplete: function(data) {
            console.log("onfido wizard finished. Notifying server.")
            let xhttp = new XMLHttpRequest()
            xhttp.open("GET", "/onfido/submit/" + applicantId, true)
            xhttp.send()
        },
        steps: [
            {
                type: 'welcome',
                options: {
                    title: 'Verify Your Identity',
                    nextButton: 'Next',
                    descriptions: [
                        "To buy crypto, we will need to verify your identity.",
                        "It will only take a couple of minutes."
                    ]
                }
            },
            'document',
            'face',
            'complete'
        ]
    });
}

let tokenReq = new XMLHttpRequest()
tokenReq.onreadystatechange = function () {
    if (this.readyState == 4 && this.status == 200) {
        initOnfido(this.responseText)
    }
}
tokenReq.open("GET", "/onfido/token/" + applicantId, true)
tokenReq.send()
