const _ = require('lodash');

const Expressive = function(app, currentEnv) {
  this.app = app;
  this.currentEnv = currentEnv || process.env.NODE_ENV;
};

Expressive.prototype.env = function(name, alias) {
  this.app[alias || name] = {};
  _.functions(this.app).forEach((fn) => {
    // Need access to the arguments object in this function, so we can't use
    // an arrow function, and therefore need to bind "this"
    this.app[alias || name][fn] = function() {
      if (this.currentEnv === name) this.app[fn].apply(this.app, arguments);
    }.bind(this);
  });
  return this;
};

var initialize = (app, currentEnv) => {
  return new Expressive(app, currentEnv);
};

initialize.Expressive = Expressive;

module.exports = initialize;
