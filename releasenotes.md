## RELEASE - 0.8.59-bfcfa35
#### New in this release: 
+ 2018-11-15 [bugfix] soknadsfrist from solr is set to new Date() is its empty
+ 2018-11-15 [PAM-2078] Legge på en hash sjekk for annonsene som kommer via solr fetch jobben.
+ 2018-11-15 [PAM-2049] Søknadsfrist kommer ut en dag feil
+ 2018-11-10 [PAM-1993, PAM-1876] Port from pam-stilling, fixes
+ 2018-11-08  [PAM-2001] Henting av oppdaterte annonser med kilde XMLStilling
+ 2018-11-08 [refactoring] Changed method signature in stillingRepository to use Optionals where necessary
## RELEASE - 0.7.52-03ae4f1
#### New in this release: 
+ 2018-11-05 [fix] endpoint internal/solr/fetch/since/{date} now returns just the number of ads added, instead of a json representation of all the ads
+ 2018-11-05 [bugfix] subtracts five days from specified fetch date
+ 2018-11-01 [bugfix] flexmar sometimes doesnt convert <br />
+ 2018-11-01 [refactor] internal api call
+ 2018-11-01 [bugfix] solr localdatetime to offset in param
## RELEASE - 0.2.39-e3790b5
#### New in this release: 
+ 2018-10-04 [FIX] bugfix updated time was not being set correctly for new Ads
+ 2018-10-03 [PAM-1517] [FIX] Det vises flexmark-kode i annonsevisningen
