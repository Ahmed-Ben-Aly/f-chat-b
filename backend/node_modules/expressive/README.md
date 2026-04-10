[![Build Status](https://travis-ci.org/tandrewnichols/expressive.png)](https://travis-ci.org/tandrewnichols/expressive) [![downloads](http://img.shields.io/npm/dm/expressive.svg)](https://npmjs.org/package/expressive) [![npm](http://img.shields.io/npm/v/expressive.svg)](https://npmjs.org/package/expressive) [![Code Climate](https://codeclimate.com/github/tandrewnichols/expressive/badges/gpa.svg)](https://codeclimate.com/github/tandrewnichols/expressive) [![Test Coverage](https://codeclimate.com/github/tandrewnichols/expressive/badges/coverage.svg)](https://codeclimate.com/github/tandrewnichols/expressive) [![dependencies](https://david-dm.org/tandrewnichols/expressive.png)](https://david-dm.org/tandrewnichols/expressive)

[![NPM info](https://nodei.co/npm/expressive.png?downloads=true)](https://nodei.co/npm/expressive.png?downloads=true)

# Expressive

## Installation

`npm install --save expressive`

## Usage

```javascript
var express = require('express');
var app = express();
var expressive = require('expressive');

// nconf setup not pictured
expressive(app, nconf.get('NODE_ENV')).env('development');

app.use('/normal-route', function(req, res, next) {
  next();
});

app.development.get('/dev-only-route', function(req, res, next) {
  next();
});
```

Expressive installs environment-specific hooks in your express app, which essentially eliminates the need to do something like

```
if (process.env.NODE_ENV === 'development') {
  app.use(/* some dev route */)
}
```

Of course, you could just use if statements like this, but I've found that expressive makes an app easier to read because all the route definitions are parallel. Additionally, testing is _slightly_ easier because you don't have to specify a `describe` for each environment. You can just assert that `app.environmentName` exists and, if you're into this kind of thing, that it gets called with the appropriate routes and handlers.

## Api

Expressive exports a single function that takes two arguments: your express app and, optionally, the current environment (defaulting to `process.env.NODE_ENV`). It returns an object with a single method: `.env`.

```
expressive(app)
```

or

```
expressive(app, currentEnvironment)
```

### .env

The `.env` method also takes two arguments: the name of the environment to add hooks for and an optional alias for that environment. This method returns the same object (`this`) so you can chain your `.env` calls.

## Example

```
var express = require('express');
var app = express();
var expressive = require('expressive');

expressive(app)
  .env('test')
  .env('smoktest')
  .env('development', 'dev') // Alias "dev" allows you to do app.dev.use instead of app.development.use
```

## Contributing

Please see [the contribution guidelines](CONTRIBUTING.md).
