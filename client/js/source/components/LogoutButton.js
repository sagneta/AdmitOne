'use strict';

import React from 'react';
import ReactButton from 'react-button';
import $ from 'jquery';

class LogoutButton extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            data: ''
        };
        this.myHeaders = new Headers();
        this.myInit = { method: 'POST',
                        headers: this.myHeaders,
                        mode: 'cors',
                        cache: 'default' };

        this.onClicked = this.onClicked.bind(this);

        this.theme = {
            disabledStyle: { background: 'gray'},
            overStyle: { background: 'blue'},
            activeStyle: { background: 'red'},
            pressedStyle: {background: 'magenta', fontWeight: 'bold'},
            overPressedStyle: {background: 'purple', fontWeight: 'bold'}
        };
    }
    

    onClicked() {
        $.ajax({
            type: "POST",
            url: "/admitone/services/authenticate/logout",
            //data: mydata,
            success: function(response, textStatus, xhr) {
                console.log("success");
                // restart the application via redirect.
                window.location = "http://localhost:8080"; 
            }.bind(this),
            error: function(xhr, textStatus, errorThrown) {
                console.log("error");
            }
        });
    }

    render() {
        return <ReactButton theme={this.theme} onClick={this.onClicked}>Logout</ReactButton>;
    }
    
}

export default LogoutButton;
