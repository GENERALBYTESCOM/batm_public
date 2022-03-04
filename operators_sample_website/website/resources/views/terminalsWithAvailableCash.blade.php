@extends('gb.headerFooter')
@section('content')
    <div class="card-body text-center" id="locations">
        <h5 class="card-title">Select location</h5>
        @error('selectedTerminal')
        <div class="alert alert-danger">{{ $message }}</div>
        @enderror
        <div class="container">
            <div class="row">
                @if(count($terminals) > 0)
                    @foreach($terminals as $terminal)
                        <div class="col-sm-4">
                            <div class="card p-0 m-1 terminals" id="{{$terminal->serialNumber}}">
                                <div class="card-header">
                                    <h6>{{$terminal->location->name}}</h6>
                                </div>
                                <div class="card-body">
                                    <div class=" d-block">
                                        <h6>{{$terminal->location->city}}</h6>
                                    </div>
                                    <div class=" d-block">
                                        <h6>{{$terminal->location->contactAddress}}</h6>
                                    </div>
                                    <div class=" d-block">
                                        <h6><a class="link-primary text-primary" target="_blank"
                                               href="http://www.google.com/maps/place/{{$terminal->location->gpsLat}},{{$terminal->location->gpsLon}}">map
                                                location</a></h6>
                                    </div>
                                    <div class=" d-block">
                                        <h6>{{$terminal->location->country}}</h6>
                                    </div>
                                </div>
                            </div>
                        </div>
                    @endforeach
                @else
                    <div class="col-sm-12">
                        <h5 class="text-danger">There is no terminal for your request</h5>
                    </div>
                @endif
            </div>
        </div>
    </div>

    <div class="container mb-5">
        <form action="/sell/create" method="get">
            @csrf
            <div class="card-deck mb-3 text-center">
                <div class="card mb-12  box-shadow">
                    <input type="hidden" name="cryptoCurrency" value="{{$cryptoCurrency}}">
                    <input type="hidden" name="fiatAmount" value="{{$amount}}" >
                    <input type="hidden" name="fiatCurrency" value="{{$fiatCurrency}}">
                    <input type="hidden" name="selectedTerminal" class="@error('selectedTerminal') is-invalid @enderror">
                    <div class="card-body">
                        <input type="submit" class="btn btn-lg btn-block btn-secondary" name="sell" value="next"
                               id="sell"/>
                    </div>
                </div>
            </div>
        </form>
    </div>
@endsection





