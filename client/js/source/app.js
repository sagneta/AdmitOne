'use strict';

import React from 'react';
import ReactDOM from 'react-dom';
import Logo from './components/Logo';
import Ping from './components/Ping';
import LoginForm from './components/LoginForm';
import LogoutButton from './components/LogoutButton';
import SearchForm from './components/SearchForm';

ReactDOM.render(
  <h1>
        <Logo /> Welcome to The App!
        <Ping />
        <LoginForm/>
        <LogoutButton/>
        <SearchForm/>

        <br/>
        <br/>
        <br/>
  </h1>,
  document.getElementById('app')
);
