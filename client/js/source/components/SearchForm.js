'use strict';

import React from 'react';
import $ from 'jquery';

class SearchForm extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            startshowid: 'Event ID Start',
            endshowid:   'Event ID End'
        };

        this.handleChangeStartShowID = this.handleChangeStartShowID.bind(this);
        this.handleChangeEndShowID = this.handleChangeEndShowID.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);

        
    }
    
    handleChangeStartShowID(event) {
        this.setState({startshowid: event.target.value});
    }

    handleChangeEndShowID(event) {
        this.setState({endshowid: event.target.value});
    }

    handleSubmit(event) {
        console.log('A startshowid was submitted: ' + this.state.startshowid);
        console.log('A endshowid was submitted: ' + this.state.endshowid);
        event.preventDefault();

        var mydata = $("form#search-form").serialize();
        console.log("mydata "+ mydata);
        $.ajax({
            type: "POST",
            url: "/admitone/services/administration/search/form",
            data: mydata,
            success: function(response, textStatus, xhr) {
                console.log("success");
            },
            error: function(xhr, textStatus, errorThrown) {
                console.log("error");
            }
        });
    }

    render() {
        return (
                <form id="search-form" onSubmit={this.handleSubmit} >
                <h1><center>Search</center></h1>
                <h2><center>Search for Events between ID</center></h2>
                <label>
                startshowid:<input name="startshowid" type="text" value={this.state.startshowid} onChange={this.handleChangeStartShowID} />
                </label>
                <p>and</p>
                <label>
                endshowid:<input name="endshowid" value={this.state.endshowid} onChange={this.handleChangeEndShowID} />
                </label>

                <p>
                <center><input type="submit" value="Search" /></center>
                </p>
                </form>
        );
    }

}

export default SearchForm;
