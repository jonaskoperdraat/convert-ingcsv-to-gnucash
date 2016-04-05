# convert-ingcsv-to-gnucash
Application to convert csv export from ING to a format GnuCash can import. The application will parse a ING CSV export file and output a new CSV file containing 8 columns matching the column options in the gnucash CSV import dialog. The columns "Nr" and "Saldo" will be empty as thsi information is not available in the ING CSV export.

## build
`mvn clean install assembly:single`

## Usage
`java -jar <jarfile> <path-to-csv-file>`

Will parse the given CVS file and output a new CSV file in the same location as the original file and with the same filename appended  with "_converted".
