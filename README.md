# mdcsvimporter2015
Moneydance plug-in to import transactions from CSV files. You can define, simply, on a screen, any number of 'Custom Readers' yourself, one for each Account file. 
- You define the order and fields that will be imported into MD, like Date, Amount, Description, Category, etc... 
- You can ignore fields, and specify that certain fields "Can Be Blank" (otherwise the cannot be blank). 
- You can test if a file parses properly, 
- and have it find and list files to import that are tied by file name to a Custom Reader. 
- You can do Regex parsing of csv files for tricky situations. 
- And importantly, it will not import transactions that you previously imported, by doing matching. 
- There is a How To doc you can download with an example of how to define your own custom readers.

Latest version: only from here - version right in Money Dance is: 15.7.7 at this time, so if you want the latest, get it from here.
Remove old version. Restart. Install via 'Add From File'.

The plug-in is still considered BETA, however, it works well for people who have tried it.

It is distributed under GNU LGPL. Among other things this means that it is free, but that the authors cannot take any responsibility for you using this code.

** Quick Usage:
This is a top down window process. I might look at getting rid of 'Date Format', but when I have time. 
You have to define a reader first of all and tell it how to match filenames. An improvement I plan to make is to tell people if they do not have one and that they need to create one first.

* 1.) So, what I do is download a new trans list csv file from my bank (XYZ)
* 2.) in MD do, "Import File" (my extension)
* 3.) I have say 6 defined so I pick the "File Reader" for my bank (XYZ).
* 4.) I hit button "Find Import File(s) for this Reader."
It populates "Select Import File:" dropdown with my list of files that match my reader "filename matcher". It also gives the number of files that match. Hopefully 1. If not I pick the file I want to import.
* 5.) Hit "Preview Import" so it validates the importing transactions.
* 6.) "Process" button becomes enabled. Hit it to do the import.
- Done.

** How to Set Up 'Filename Matcher':    (you match with a regex, regular expression, look that up)
`Here are some examples:`

`.*\.(csv|CSV) .* means match anything, then it has to end with a dot "\." and either csv or CSV`
`(this|that)   matches "this" or "that"`

`download.*\.(csv|CSV) almost the same but it has to begin with "download" then anything, then .csv or .CSV`

`for VISA: `
`Transactions_\d+_\d+.csv matches Transactions_(1 or more numbers)_(1 or more numbers).csv`
`like: Transactions_20170325_214425.csv`

`for Discover: `
`(DFS-|Discover).*\.(csv|CSV) matches DFS- or Discover, then anything, then .csv or .CSV`
`like DFS-whatever.csv   or   Discover.1234.CSV`

** regex field parsing changed in v21 to hopefully give more flexibility. you need to use "named capture groups", as in: 
`"?(?<value>.*?)"?(?:[,]|\Z)(?<rest>.*)`

value = what string you want to pull out for the field value.
rest = is left over line to parse next.

if you have this line:
01/14/2018,check 3000,My Store,$123.40,whatever

it will parse like this:
get value = 01/14/2018
rest = check 3000,My Store,$123.40,whatever

get value = check 3000
rest = My Store,$123.40,whatever

get value = My Store
rest = $123.40,whatever

get value = $123.40
rest = whatever

get value = whatever
rest = 
