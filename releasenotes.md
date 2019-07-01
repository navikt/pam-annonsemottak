## RELEASE - 0.51.169-af2c139
#### New in this release: 
+ 2019-06-18 [Fjera solr-integrasjon og duplikatsjekk mot arena]
## RELEASE - 0.50.167-6c40036
#### New in this release: 
+ 2019-06-20 [Stillinger med source lik nav fra amedia filtreres bort slik at vi unngår at stillingene går i loop mellom amedia og nav] + noe refaktorering
## RELEASE - 0.49.164-caca12d
#### New in this release: 
+ 2019-05-28 [PAM-3354,PAM-3424] Endre datoer knyttet til publisering (inkl. stoppe annonse).
+ 2019-05-06 [CFG] Enable mappings metadata endpoint
## RELEASE - 0.48.161-772d6b6
#### New in this release: 
+ 2019-04-08 [Stillinger med stopp-dato tilbake i tid skal nå automatisk få stoppet-status]
+ 2019-04-04 [Fjernet featuretoggle pam.schedule.fetch.from.xmlstilling slik at den alltid er aktiv]
## RELEASE - 0.47.158-e816db9
#### New in this release: 
+ 2019-03-27 [Legger på dummy-tittel hvis tittel mangler fra xml-stilling]
+ 2019-03-26 [fix] pom.xml to reduce vulnerabilities (#38)
## RELEASE - 0.45.149-9ae2695
#### New in this release: 
+ 2019-03-25 [Kan nå ta imot informasjon om når en stilling mottatt via XmlStilling er ledig fra]
## RELEASE - 0.41.143-9463b75
#### New in this release: 
+ 2019-03-18 [PAM-2969] Endringer i Finn-data, navn på arbeidsgiver har blitt til attributt i XML
## RELEASE - 0.40.137-b4a17d9
#### New in this release: 
+ 2019-01-23 [FIX] disable postgres use_jdbc_metadata_defaults
## RELEASE - 0.38.122-cf77872
#### New in this release: 
+ 2019-01-15 [bugfix] replaces robotName to kommune name as medium for Kommuner crawlers
## RELEASE - 0.34.117-b3d1e0d
#### New in this release: 
+ 2019-01-09 [Feature] metering source and origin in annonsemottak
## RELEASE - 0.33.116-1c8aa9e
#### New in this release: 
+ 2019-01-02 [PAM-1604] Metrikker for annonsemottak
## RELEASE - 0.32.115-2374a8b
#### New in this release: 
+ 2019-01-03 [PAM-2417] Sette et filter på Jobbnorge
+ 2019-01-03 [Upgrade] Spring boot to 2.1.1 and shedlock to 2.2.0
## RELEASE - 0.30.112-5c76f42
#### New in this release: 
+ 2019-01-02 [FIX] Broaden match for PAM-cookie in DIR-ads
+ 2018-11-26 [UPGRADE] update to spring boot 2.1.0
## RELEASE - 0.29.109-acf593f
#### New in this release: 
+ 2018-12-13 [bugfix] null handling in URL for polaris ads
+ 2018-12-13 [bugfix] null handling in URL for polaris ads
## RELEASE - 0.28.107-839d005
#### New in this release: 
+ 2018-12-10 [bugfix] polaris ad url mapping
## RELEASE - 0.27.105-6d98caa
#### New in this release: 
+ 2018-12-05 [PAM-2230] enable scheduler for polaris
## RELEASE - 0.24.98-a2131f4
#### New in this release: 
+ 2018-12-03 [PAM-2004] Hente data fra Polaris Media
+ 2018-12-02 [REFACTORING] Cleans up code after code inspection.
+ 2018-12-02 [REFACTORING] Extracts config from common into explisit config.
+ 2018-12-02 [REFACTORING] Renamed package names
+ 2018-12-02 [PAM-2186] Replaces use of gauge with counter
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
