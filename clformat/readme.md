CLFormat
========

An implemention of something strongly reminiscent of the Common Lisp FORMAT
string-formatting function.

Directives
----------

FORMAT strings are made up of free-form text, interspersed with directives. A
directive is always started with a ~, and consists of the following parts.
1. An optional set of prefix parameters
2. An optional set of directive modifiers
3. The name of the directive.

### Prefix parameters
Prefix parameters are used to configure options for a directive. These include
things the number of columns to print a field in, or what radix to print a
number in. They are separated from each other by commas.

A prefix parameter can be one of the following:
* A signed decimal number
* A single character, preceeded by a single quote `'`
* The letter `V`\
    Specifies that the value for this parameter should be read from the
	parameters to the format string
* The character `#`\
    Specifies that the value for this parameter should be the number of
	parameters to the format string remaining
* The character `%`\
    Specifies that the value for this parameter should be the current position
	in the format string parameter list
* A double quoted string.

Prefix parameters are normally referred to positionally, but they can be given a
name by starting the parameter with a `#` character, following it with a
parameter name (which can be any series of printing characters that is not a
whitespace character and is not one of the following seperator characters `,`,
`:`, `;`), then a name-separator (`:` or ';`) followed by one of the values, as
specified above.

Whether or not a named parameter can also be referred to positionally is
determined by the name-separator: `;` says it can, `:` says it can't.

As for specifying more parameters than the directive takes, the rules are as
follows:
* You may specify as many non-positional parameters as you want
* You may not specify more or less positional parameters than the directive says
  it takes

In addition, as a useful feature, parameter names may be abbreviated to the
shortest possible unique value. For example, if a directive took the following
parameters (`foo`, `foobar` and `fizz`), the following would be valid specifiers:
* `foo` (refers to `foo`)
* `foobar` (refers to `foobar`)
* `foob` (refers to `foobar`)
* `fizz` (refers to `fizz`)
* `fi` (refers to `fizz`)
* `f` (not allowed, ambiguous reference)
### Modifier sets
A modifier set is any combination of the following characters, with duplicate
characters having no effect: 
```
* $
* :
* *
* @
```

### Directive names
A directive name can be either:
* A single non-whitespace, non-/ character that is optionally preceded by a
  grave
* A name enclosed in /'s

If it is a name enclosed in /'s, then it is a call to a user specified function.
Otherwise, it is an invocation of one of the built-in directives.

## Directives
The following is a list of all of the directives that are currently
implemented, as well as a short description of what that directive does

| Directive | Name | Description|
|-----------|------|------------|
| A   | Aesthetic   | General string printer. |
| S   |             | This is an alias for the A directive |
| C   | Character   | Print out a single character |
| B   | Binary      | Print out a base-2 integer |
| O   | Octal       | Print out a base-8 integer |
| D   | Decimal     | Print out a base-10 integer |
| X   | Hexadecimal | Print out a base-16 number |
| R   | Radix       | Print out a integer in an arbitrary base |
| &   | Freshline   | Print out a newline, if we didn't just print one |
| %   | Newline     | Print out a literal new line (\n) |
| \|  | Formfeed    | Print out a formfeed character and start a new page |
| ~   | Tilde       | Print out a literal tilde |
| ?   | Recursive   | Invoke a sub-format string |
| *   | Go-to       | Move around in the parameter list |
| ^   | Escape      | Escape from an enclosing directive |
| [   | Conditional | Select a format string, based on a condition |
| {   | Iteration   | Repeatedly invoke a format string. |
| (   | Case        | Perform case-manipulation on text |
| \`[ | Inflection  | Perform inflection on a format string |
| T   | Tabulate    | Print something in a table-like format |

The following are directives that are not valid to use outside of specific other
directives

| Directive | Name | Description|
|-----------|------|------------|
| ]   | Conditional end | End a conditional directive
| ;   | Clause separator | Separate two clauses in a block directive
| )   | Case end | End a case directive
| \`] | Inflection end | End a inflection directive
| <   | Inflect start | Start a inflection control |
| >   | Inflect end | End a inflection control |

The following are directives that have not been implemented

| Directive | Name | Description|
|-----------|------|------------|
| \`< | Layout start | Start a layout controlling block |
| \`> | Layout end | End a layout controlling block |
| F   | Fixed-Format floating point | Print a floating point number with a fixed number of characters|
| E   | Exponential floating point | Print a engineering-style floating point number |
| G   | General floating point | Print a floating point number |
| $   | Monetary floating point | Print a floating point number as currency |
| W   | String print | Use A directive instead 
| P   | Plural | Use the \`[ and \`] directives instead

The next section of the document is a reference for each directive type,
detailing how that directive works, how many and what sort of parameters it
takes, and all of that sort of thing

## A Directive
The A directive is the general purpose string formatting directive, serving as
roughly analagous to the S directive in many printf-type strings.

It takes zero, one or four parameters (described by the table below), as well as one argument.

| Sample String | Description |
|---------------|-------------|
| ~a | Print out the next format argument as a string. |
| ~mincol:[0]a | Print out the next format argument, taking at least *mincol* columns | 
| ~mincol:[0],colinc:[1],minpad:[0],padchar:[' ]a | Print out the next format argument, adding at least *minpad* instances of *padchar*, then adding copies of *padchar* in *colinc* sized blocks until the total output size is at least *mincol* columns long. |

Padding will be inserted after the string, unless the `@` modifier has
been specified, in which case it will go before the string.

## ( Directive
The ( directive is used for performing simple case-mappings on a string. It
encloses a string that contains other directives, terminated by the ~)
directive.

It takes no parameters, but takes one format argument; its behavior is
influenced by the modifiers provided to the ( directive, but not much else.
