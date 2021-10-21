@extends('gb.headerFooter')
@section('content')

    <div class="container mt-5">
        @if ($error ?? '')
            <div class="alert alert-danger">
                <ul>
                    <li>{{ $error }}</li>
                </ul>
            </div>
        @endif
        <form action="/trade" method="get">
            @csrf
            <div class="card-deck mb-3 text-center">
                <div class="card mb-4 box-shadow">
                    <div class="card-body">
                        <input type="submit" class="btn btn-lg btn-block btn-outline-primary" name="buy" value="buy" id="buy">
                    </div>
                </div>
                <div class="card mb-4 box-shadow">
                    <div class="card-body">
                        <input type="submit" class="btn btn-lg btn-block btn-outline-primary" name="sell" value="sell" id="sell">
                    </div>
                </div>
            </div>
        </form>
    </div>
    <div class="card-body text-center" id="locations">
        <h5 class="card-title">Our locations</h5>
        <div class="container">
            <div class="row">
                @if ($terminals ?? '')
                    @foreach($terminals as $terminal)
                        <div class="col-sm-4">
                            <div class="card p-0  m-1">
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
                                        <h6><a class="link-primary text-primary" target="_blank" href="http://www.google.com/maps/place/{{$terminal->location->gpsLat}},{{$terminal->location->gpsLon}}">map location</a></h6>
                                    </div>
                                    <div class=" d-block">
                                        <h6>{{$terminal->location->country}}</h6>
                                    </div>
                                </div>
                            </div>
                        </div>
                    @endforeach
                @endif
            </div>
        </div>
    </div>
@endsection





