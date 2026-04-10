_ = require 'lodash'

describe 'expressive', ->
  Given -> @app =
    use: sinon.stub()
    foo: sinon.stub()
    bar: sinon.stub()
  Given -> @subject = require '../lib/expressive'

  describe 'with no current env defaults to process.env.NODE_ENV', ->
    Given -> process.env.NODE_ENV = 'development'
    When -> @subject(@app).env('development')
    And -> @app.development.use('foo')
    Then -> expect(@app.use.calledWith('foo')).to.be.true()

  describe 'with a current env', ->
    When -> @subject(@app, 'bar').env('bar')
    And -> @app.bar.use('foo')
    Then -> expect(@app.use.calledWith('foo')).to.be.true()

  describe 'with a non matching env', ->
    When -> @subject(@app, 'bar').env('quux')
    And -> @app.quux.use('foo')
    Then -> expect(@app.use.called).to.be.false()

  describe 'with an alias', ->
    When -> @subject(@app, 'development').env('development', 'dev')
    And -> @app.dev.use('foo')
    Then -> expect(@app.use.calledWith('foo')).to.be.true()
