const urlParams = new URLSearchParams(window.location.search)
const applicantId = urlParams.get('a')
const supportedLanguages = ["cs", "en_US", "es_ES", "de_DE", "fr_FR"]

var phrases = {}
var locale = ""

function getBaseUrl() {
    let base = window.location.pathname
    if (!base.endsWith("/")) {
        base = base + "/"
    }
    return base
}
let baseUrl = getBaseUrl()

function loadLang(onCompleteF) {
    locale = urlParams.get('lang')
    if (!locale || !supportedLanguages.includes(locale)) {
        locale = "en_US"
    }

     // create script tag and call onComplete func when script is loaded
    var script = document.createElement('script')
    script.onload = function () {
        phrases = langData
        onCompleteF()
    }
    script.onerror = onCompleteF()
    script.src = "i18n/lang." + locale + ".js"
    document.head.appendChild(script)
}

function initOnfido(token) {

    Onfido.init({
        useModal: false,
        token: token,
        containerId: 'onfido-mount',
        onComplete: function(data) {
            console.log("onfido wizard finished. Notifying server.")
            let xhttp = new XMLHttpRequest()
            xhttp.open("GET", baseUrl + "verification/submit/" + applicantId, true)
            xhttp.send()
        },
        language: {
            locale: locale,
            phrases: phrases
        },
        steps: [
            'welcome',
            'document',
            'face',
            'complete'
        ]
    });
}

function getToken() {
    let tokenReq = new XMLHttpRequest()
    tokenReq.onreadystatechange = function () {
        if (this.readyState == 4 && this.status == 200) {
            initOnfido(this.responseText)
        }
    }
    tokenReq.open("GET", baseUrl + "verification/token/" + applicantId, true)
    tokenReq.send()
}

loadLang(getToken)
