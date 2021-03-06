'use strict';

import React from 'react';
import ReactDOM from 'react-dom';
import LoginForm from './components/LoginForm';
import LogoutButton from './components/LogoutButton';
import SearchForm from './components/SearchForm';

ReactDOM.render(
        <h1>
        <div>
        <LoginForm/>
        <br/>
        <br/>
        <br/>
        <LogoutButton/>
        </div>
        </h1>,
  document.getElementById('app')
);
