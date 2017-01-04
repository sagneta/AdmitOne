'use strict';

import React from 'react';
import SearchForm from './SearchForm';
import $ from 'jquery';

class LoginForm extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            username: 'Username',
            password: 'Password',
            displaySearch: false
        };

        this.handleChangeUsername = this.handleChangeUsername.bind(this);
        this.handleChangePassword = this.handleChangePassword.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);

        this.setState = this.setState.bind(this);
    }
    
    handleChangeUsername(event) {
        this.setState({username: event.target.value});
    }

    handleChangePassword(event) {
        this.setState({password: event.target.value});
    }

    handleSubmit(event) {
        //console.log('A username was submitted: ' + this.state.username);
        //console.log('A password was submitted: ' + this.state.password);
        event.preventDefault();

        var mydata = $("form#login-form").serialize();
        //console.log("mydata "+ mydata);
        $.ajax({
            type: "POST",
            url: "/admitone/services/authenticate/loginform",
            data: mydata,
            success: function(response, textStatus, xhr) {
                console.log("success");
                this.setState(
                    {
                        displaySearch: true
                    });
            }.bind(this),
            error: function(xhr, textStatus, errorThrown) {
                console.log("error");
            }
        });
    }

    render() {
        if(this.state.displaySearch) {
            return (
                    <div>
                    <SearchForm/>
                    </div>
            );
        } else {
        return (
                <div id="loginform">
                <form id="login-form" onSubmit={this.handleSubmit}>
                <h1><center>Login</center></h1>
                <label>
                <input id="loginform-username" name="username" type="text" value={this.state.username} onChange={this.handleChangeUsername} />
                </label>
                <br/>
                <label>
                <input id="loginform-password" name="password" type="password" value={this.state.password} onChange={this.handleChangePassword} />
                </label>
                <br/>
                <input id="loginbutton" type="submit" value="Login" />
                </form>
                </div>
        );
        }
    }

}

export default LoginForm;
