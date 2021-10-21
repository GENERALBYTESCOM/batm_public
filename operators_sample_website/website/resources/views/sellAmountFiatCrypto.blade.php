@extends('gb.headerFooter')
@section('content')
    <div class="container mt-5 mb-5">
        <form action="/sell/show" method="get">
            @csrf
            @if ($errors->any())
                <div class="alert alert-danger">
                    <ul>
                        @foreach ($errors->all() as $error)
                            <li>{{ $error }}</li>
                        @endforeach
                    </ul>
                </div>
            @endif
            <div class="card-body text-center">
                <h5 class="card-title">How much you want to pick up</h5>
            </div>
            <div class="card mb-12 box-shadow mb-3">
                <div class="card-body">
                    <input type="number" class="btn btn-lg btn-block @error('amount') is-invalid @enderror" name="amount" placeholder="100" id="sell">
                    @error('amount')
                    <div class="alert alert-danger">{{ $message }}</div>
                    @enderror
                </div>
            </div>
            <div class="card mb-12 box-shadow mb-3">
                <div class="card-body">
                    <select class="btn btn-lg btn-block " name="fiat"  id="fiat">
                        @foreach($fiats as $fiat)
                            <option value="{{$fiat}}">{{$fiat}}</option>
                        @endforeach
                    </select>
                </div>
            </div>            <div class="card mb-12 box-shadow mb-3">
                <div class="card-body">
                    <select class="btn btn-lg btn-block " name="cryptoCurrency"  id="cryptoCurrency">
                        @foreach($cryptoCurrencies as $cryptoCurrency)
                            <option value="{{$cryptoCurrency}}">{{$cryptoCurrency}}</option>
                        @endforeach
                    </select>
                </div>
            </div>
            <div class="card-deck mb-3 text-center">
                <div class="card mb-12 box-shadow">
                    <div class="card-body">
                        <input type="submit" class="btn btn-lg btn-block btn-secondary" name="sell" value="next" id="sell">
                    </div>
                </div>
            </div>
        </form>
    </div>
@endsection





