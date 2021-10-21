@extends('gb.headerFooter')
@section('content')
    <div class="card-body text-center">
        @csrf
        <div class="container">
            <div class="row">
                <div class="col-md-12  justify-content-center text-center mt-4">
                    <a href="bitcoin:{{$sellInfo->cryptoAddress}}?amount={{$sellInfo->cryptoAmount}}&label={{$sellInfo->remoteTransactionId}}&uuid={{$sellInfo->transactionUUID}}">
                        <div id="qrcode" class="mb-5">
                        </div>
                        <script type="text/javascript">
                            new QRCode(document.getElementById("qrcode"), "{{$cryptoFullName}}:{{$sellInfo->cryptoAddress}}?amount={{$sellInfo->cryptoAmount}}&label={{$sellInfo->remoteTransactionId}}&uuid={{$sellInfo->transactionUUID}}");
                        </script>
                    </a>
                </div>
                <div class="col-md-12 card p-0  m-3">
                    <div class="card-header">
                        <h6>Please take a photo of the QR code and
                            send {{ sprintf('%f',$sellInfo->cryptoAmount) . ' ' . $sellInfo->cryptoCurrency}} to address below</h6>
                    </div>
                    <div class="card-body">
                        <div class=" d-block">
                            <h6>{{$sellInfo->cryptoAddress}}</h6>
                        </div>
                        <div class=" d-block">
                            <h6>Validity: <span id="validity">{{$sellInfo->validityInMinutes}} min</span></h6>
                        </div>
                    </div>
                </div>
                <div class="col-md-12 card p-0  m-3">
                    <div class="card-header">
                        <h6>Transaction id: {{$sellInfo->transactionUUID}} </h6>
                    </div>
                    <div class="card-body text-left">
                        <div class=" d-block">
                            <div class="row">
                                <div style="margin: auto">
                                    <div class="loader float-left ml-1"></div>
                                    <div class="float-left mx-2"><span>Status: </span><span id="status" class="text-center"></span></div>
                                    <div class="loader float-left ml-1"></div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <style>
        #qrcode > img {
            display: inline !important;
        }
    </style>
    <script type="text/javascript" src="{{asset('/assets/js/statusInfo.js')}}"></script>
    <script type="text/javascript" src="{{asset('/assets/js/countDown.js')}}"></script>
    <script>
        startAjax("{{$sellInfo->remoteTransactionId}}")
        setInterval(function () {
            startAjax("{{$sellInfo->remoteTransactionId}}");
        }, 50000)
    </script>
@endsection





