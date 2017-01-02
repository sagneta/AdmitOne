'use strict';

import React from 'react';
import $ from 'jquery';

class Ping extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            text: 'nothing'
        };
        this.myHeaders = new Headers();
        this.myInit = { method: 'GET',
                        headers: this.myHeaders,
                        mode: 'cors',
                        cache: 'default' };

    }
    
    render() {
        return <div className="Ping">Response - {this.state.data}</div>;
    }

    componentDidMount() {
        console.log("Ping is working and will make an ajax invocation");

        fetch('/admitone/services/administration/ping', this.myInit)
            .then( (response) => {
                console.log("Response: " + response.ok);
                return (response.ok) ? response.json() : {status: 'Failure'};
            })   
            .then( (json) => {
                console.log("we are done: " + json.status);
                this.setState({data: json.status});
            });
    }
}

export default Ping;
