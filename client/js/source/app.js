'use strict';

import React from 'react';
import ReactDOM from 'react-dom';
import Logo from './components/Logo';
import Ping from './components/Ping';
import LoginForm from './components/LoginForm';
import $ from 'jquery';

ReactDOM.render(
  <h1>
        <Logo /> Welcome to The App!
        <Ping />
        <LoginForm/>
  </h1>,
  document.getElementById('app')
);
