## RELEASE - 0.22.91-b311391
#### New in this release: 
+ 2018-11-29 [refactor] makes WireMock reusable in other tests
## RELEASE - 0.20.82-0b29f32
#### New in this release: 
+ 2018-11-26 [REFACTORING] Moves scheduler to stilling package.
## RELEASE - 0.17.74-31178f9
#### New in this release: 
+ 2018-11-22 [PAM-2145] Fetch ads registered by NAV Servicesenter from Solr
+ 2018-11-22 [bugfix] after a scheduled run, many ads were stopped (#14)
## RELEASE - 0.16.71-eca63d7
#### New in this release: 
+ 2018-11-22 [FIX] REST API option to save all regardless of hash, check status as well
## RELEASE - 0.13.67-c2d0eea
#### New in this release: 
+ 2018-11-19 Revert "[TEMP] Disable hash check temporarily, new refresh from stillingsolr needed"
## RELEASE - 0.12.65-08f59c1
#### New in this release: 
+ 2018-11-19 [TEMP] Disable hash check temporarily, new refresh from stillingsolr needed
+ 2018-11-19 [bugfix] solr disables active ads (#11)
## RELEASE - 0.11.62-45ec6ee
#### New in this release: 
+ 2018-11-19 Revert "[TEMP] Temporarily disable hash check to get full refresh of stillingsolr ads"
## RELEASE - 0.10.61-bac0469
#### New in this release: 
+ 2018-11-19 [TEMP] Temporarily disable hash check to get full refresh of stillingsolr ads
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
