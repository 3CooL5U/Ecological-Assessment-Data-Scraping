# Ecological-Assessment-Data-Scraping
Scripts for Scraping both EA and SEA files.

SEA files have another function called check() which prints out the immediate sentence
  that the keywords are found in, use this to confirm flagged assessments manually

There are three different versions of the SEA script as so far, I have found three 
  different variants in the way the forms have been filled out, the main difference 
  is how we find the scores in the pdfs.

These programs use libraries such as Apache PDFBox, if the imports do not work please 
  make sure these are properly installed and the build path is correct. (they can be
  found in /lib)
