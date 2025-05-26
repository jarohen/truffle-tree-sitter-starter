/// <reference types="tree-sitter-cli/dsl" />
// @ts-nocheck

const SIGN = '[-+]';
const INT = '(0|[1-9][0-9]*)';

module.exports = grammar({
  name: "intlang",

  rules: {
    source_file: $ => repeat($._form),

    _form: $ => choice($.int),

    int: _ => token(new RegExp(`${SIGN}?${INT}`)),
  }
});
