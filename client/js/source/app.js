'use strict';

import React from 'react';
import ReactDOM from 'react-dom';
import Logo from './components/Logo';
import Ping from './components/Ping';
import $ from 'jquery';

ReactDOM.render(
  <h1>
        <Logo /> Welcome to The App!
        <Ping /> 
  </h1>,
  document.getElementById('app')
);
