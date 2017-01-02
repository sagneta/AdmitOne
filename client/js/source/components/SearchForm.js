'use strict';

import ReactButton from 'react-button';
import Excel from './Excel';
import React from 'react';
import $ from 'jquery';

class SearchForm extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            startshowid: 'Event ID Start',
            endshowid:   'Event ID End',

            headers: ['Event ID', 'Customer', 'Tickets'],
            data: [['9', 'Joe Blow', '101']],
            displayExcel: false
        };

        this.handleChangeStartShowID = this.handleChangeStartShowID.bind(this);
        this.handleChangeEndShowID = this.handleChangeEndShowID.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
        this.onClicked = this.onClicked.bind(this);
        
    }
    
    handleChangeStartShowID(event) {
        this.setState({startshowid: event.target.value});
    }

    handleChangeEndShowID(event) {
        this.setState({endshowid: event.target.value});
    }

    handleSubmit(event) {
        console.log('A startshowid was submitted: ' + this.state.startshowid);
        console.log('A endshowid   was submitted: ' + this.state.endshowid);
        event.preventDefault();

        var mydata = $("form#search-form").serialize();
        console.log("mydata "+ mydata);
        $.ajax({
            type: "POST",
            url: "/admitone/services/administration/search/form",
            data: mydata,
            success: function(results) {
                //console.log("success: " + JSON.stringify(results));

                var purchases = [];
                results.forEach(function(row) {
                    //console.log("ROW:" + JSON.stringify(row));
                    purchases.push([row.toShowID, row.username, row.tickets]);
                });
                this.setState(
                    {
                        displayExcel: true,
                        data: purchases 
                    }
                );
            }.bind(this),
            error: function(xhr, textStatus, errorThrown) {
                console.log("error");
            }
        });
    }

    onClicked() {
        this.setState(
                    {
                        displayExcel: false
                    }
        );
    }
    
    render() {

        if(this.state.displayExcel) {
            return (
                    <div>
                    <ReactButton onClick={this.onClicked}>Search Again</ReactButton>
                    <Excel headers={this.state.headers} initialData={this.state.data}/>
                    </div>
            );
        } else {
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

                    <br></br>
                    <input type="submit" value="Search" />
                    </form>
            );
        }
    }

}

export default SearchForm;
