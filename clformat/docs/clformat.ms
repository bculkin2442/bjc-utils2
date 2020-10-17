\# CLFormat documentation
\# Load macros and stuff
.so clformat.tmac
.TL
CLFormat Documentation
.AU
Ben Culkin
\# No abstract needed
.AB no
.AE
.PP
CLFormat is an implementation of something that is strongly reminiscent of the
Common Lisp FORMAT function. I say 'strongly reminiscent', because we don't
support quite all the directives they, but support several new ones they don't.
.Section "Directive Syntax"
FORMAT strings are made up of free-form text, with all of the functionality
coming from \fIdirectives\fP. A directive is always started with a tilde, and
consists of the following parts:
.Numlist
.Numitem
.Italic "An optional set of " "prefix parameters" "."
.Numitem
.Italic "An optional set of " "directive modifiers" "."
.Numitem
The name of the directive
.PP
To execute a format string, and get it to provide formatting, a set of arguments
called \fIformat items\fP must be provided. These serve the same purpose as the
arguments provided to printf and other similar functions after their format
string.
.Subsection "Prefix Parameters"
Prefix parameters are used to configure the options for a directive. These can
include things varying from the number of columns to print a field in, to the
radix that a number should be printed in. They are separated from each by
commas.
.PP
A prefix parameter can be any one of the following:
.Defn "A signed decimal number"
The value of this parameter is the provided number.
.Defn "A single character, preceeded by a single-quote"
The value of this parameter is the provided character
.Defn "The letter V"
The value of this parameter will be read from the format string items.
.Defn "The character #"
The value of this parameter will be set to the number of format string
parameters remaining
.Defn "The character %"
The value of this paramter will be set to the current position in the list
of format string items.
.Defn "A double quoted string"
The value of this parameter is the represented string.
.PP
Prefix parameters are numbered positionally by default, but they can be given a
name by starting the parameter with a # character, following that with a
parameter name (which can be any series of printing characters that is not a
whitespace character, and not one of the following separator characters: ',',
',', ':' or ';'). It is then followed by a name-separator character (either ':'
or ';') then its value from the above list.
.Supersection "Notes"
.Bullet
The difference between ':' and ';' is that if you use ':', that
parameter can't be addressed positionally, while if you use ';', it still can.
.Bullet
If you specify more parameters than a directive is noted to take, the behavior
is based off of whether you've provided extra named, non-positional parameters or extra
positional ones. You can specify as many named non-positional parameters as you
want, and the directive will be fine. However, if you specify more or less
positional parameters, that is an an error.
.Bullet
As a 'useful' feature, when you are naming named parameters, you may abbreviate
the name to its shortest unambigous value. However, as an exception to the
normal rule that you can provide unused named parameters, passing an ambiguous
parameter name will cause an error. For instance, if a directive took the
parameters 'foo', 'foobar' and 'fizz', these are some of the results:
.RS
.CWDefn "foo"
	Refers to foo
.CWDefn "foobar"
	Refers to foobar
.CWDefn "foob"
	Refers to foobar
.CWDefn "fizz"
	Refers to fizz
.CWDefn "fi"
	Refers to fizz
.CWDefn "f"
	Error, could refer to foo, foobar or fizz
.RE
.Endsupersection
.Endsubsection
.Subsection "Directive modifiers"
Directive modifiers, or \fImodifier sets\fP, are any combination of the
following characters (with duplicate characters having no effect)
.Bullet
.CW "$"
.Bullet
.CW ":"
.Bullet
.CW "*"
.Bullet
.CW "@"
.Endsubsection
.Subsection "Directive names"
A directive name can be either
.Bullet
A single, non-whitespace, non-/ character, optionally preceeded by a grave
.Bullet
A name bracketed by /
.PP
If the directive is a bracketed name, then it is a call to a user-specified
function. Otherwise, it is an invocation of one of the built-in directives.
.Endsubsection
.Endsection
.Section "Directive List"
The following is a table of all of the directives that are currently implemented,
as well as a short description of what each directive does.
.KS
.TS H
center allbox ;
c s s
c | c | c .
Directive Overview
Directive	Name	Brief Description
_
.TH
.T&
lfCR | l | l .
A	Aesthetic	General string printer.
S		Alias for the A directive\[dg]
C	Character	Print out a single character
B	Binary	Print out a base-2 integer
O	Octal	Print out a base-8 integer
D	Decimal	Print out a base-10 integer
X	Hexadecimal	Print out a base-16 integer
R	Radix	Print out an integer in an arbitrary base\[bu]
&	Fresh Line	Print a newline character, if we didn't just print one
%	Newline	Print out a literal new line
|	Formfeed	Print out a 'formfeed' and start a new page
~	Tilde	Print out a literal tilde
?	Recursive	Invoke a sub-frmat string
*	Go-to	Move around in the item list
^	Escape	Escape from an enclosing directive
[	Conditional	Select a sub-format string, based on a conditional
{	Iteration	Repeatedly invoke a sub-format string
(	Case	Perform case-manipulation on text
\`[	Inflection	Perform inflection (pluralization/singularization) on a sub-format string
T	Tabulate	Print items in a tabular format
.TE
.ce 1
\fITable 1: Directive Overview\fP
.KE
.FS \[dg]
CommonLisp has S use a slightly different format, but there isn't one that makes
sense for us to use. In the future, we may either come up with a format, or
reassign S to do something else entirely
.FE
.FS \[bu]
At some point, it may make sense to remove some of the radix-specific ones, and
just tell people to use R. However, for now, we're sticking with it.
.FE
.PP
The following are directives that aren't valid for use outside of a specific
context (usually, they must follow another directive
.KS
.TS H
center allbox ;
c s s
c | c | c .
Context-Sensitive Directives
Directive	Name	Brief Description
_
.TH
.T&
lfCR | l | l .
]	End Conditional	Ends a conditional directive
;	Clause Separator	Separates two clauses in a block directive
)	End Case	Ends a case directive
\']	End Inflection	Ends a inflection directive
<	Inflection Start 	Starts an inflection control
>	Inflection End	Ends an inflection control
.TE
.ce 1
\fITable 2: Context-Sensitive Directives\fP
.KE
.PP
And the following are directives which have not yet been implemented
.KS
.TS H
center allbox ;
c s s
c | c | c .
Unimplemented Directives
Directive	Name	Brief Description
_
.TH
.T&
lfCR | l | l .
\`<	Start Layout	Start a layout-control block
\`>	End Layout	Ends a layout-control block
F	Fixed-Format Float	Print a floating-point number in a fixed-point format
E	Exponential Float	Print a engineering-style (1.2e2) floating point number
G	General Float	Print a general floating point number
$	Monetary Float	Print a floating point number like currency
W	String Print	Use the A directive instead
P	Plural	Use the `[ and `] directives instead
.TE
.ce 1
\fITable 3: Unimplemented Directives\fP
.KE
.PP
The following section contains a detailed reference for the directives mentioned
above. This details how the directive works, how many parameters it takes, and
of what sort they should be, and whatever other details are necessary for that
particular directive.
.Subsection "A Directive"
.PP
The A directive is the general purpose string formatting directive, serving as
the equivalent to the S specifier in printf and other similar formats.

It takes either zero, one or four parameters, as well as one format string item.
.KS
.TS
allbox;
c c
lfCR l .
Sample Format String	Description
~a	Prints out the format string item as a string
.TE
.KE
.Endsubsection
.Endsection
