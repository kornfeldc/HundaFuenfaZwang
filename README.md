## 1.1.8 (02.06.2018)
#### Changed
* Message when trying to add a new sale for a day which is not TODAY
* All unpayed sales are now showed on TODAY
* Replaced floating action buttons with full sized buttons

## 1.1.7 (12.05.2018)
#### Added
* When making changes to a sale and pressing back instead of save a message appear if you want to save the changes

#### Fixed
* Person Activity did not close after saving when offline

## 1.1.6 (05.05.2018)
#### Fixed
* Show "Done" Button when virutal keyboard is opened in pay activity
* When creating a new sale and choosing a person, check if there is already an opened sale for this person and use this one instead of a new one

## 1.1.5 (25.03.2018)
#### Added
* Added "static" article for buying Credit
* Adding this article to a sale, add the amount to the credit balance of the person

#### Changed
* Sale list and Sale menu now calculates the sum WITHOUT the amount payed from credit

## 1.1.4 (22.03.2018)
#### Fixed
* Fixed Article creation (hangup)
* Fixed hiding fab in articlechooser

#### Added
* Added Today line in SaleMenu even if there's no sale for today

## 1.1.3 (18.03.2018)
#### Added
* Added linked persons
* Hide credit in person activity when person is linked as "slave"

## 1.1.2 (17.03.2018)
#### Fixed
* Fix in +/- tip
* InclTip cant go below topay now

## 1.1.1 (12.03.2018)
### Fixed
* Last item in lists above BottomNavigation not visible

## 1.1.0 (10.03.2018)
### Changed
* Changed tip estimate behaviour
* Cannot click on person card in payed sale anymore
* Hide add/remove buttons in payed sale
* Show day in sale, choose day on click
* Sale Menu for overviewing all sale days
* Infotext in sales when there is no sale for the choosen day
* Better fab behaviour (hide on scroll down)
* Login hidden from git

## 1.0.9 (10.03.2018)
### Added
* Added group headers with sums in in sales list

## 1.0.8 (10.03.2018)
### Fixed
* Fixed wrong sum (sum without tip) in sales list

## 1.0.7 (10.03.2018)
### Fixed
* Fixed app name

## 1.0.6 (10.03.2018)
### Fixed
* Fixed several crashes

## 1.0.5 (10.03.2018)
### Added
* MTS

## 1.0.4 (10.03.2018)
### Added
* Authentification

## 1.0.3 (10.03.2018)
### Changed
* Version raised to check if it has influence on firestore behaviour

## 1.0.2 (09.03.2018)
### Added
* Pay activity done
* Add credit to person again when sale with used credit is deleted

## 1.0.1 (08.03.2018)
### Fixed
* Loading text didn't disappear
* Direct sale: no name displayed in sale list
* Localized currency format

### Changed
* Add credit now sets 20 as default value
* Card "Actual selection" has now a better visibility (accent color)

### Added
* Added possibility to delete a sale

---

## 1.0.0 (21.02.2018)
### Added
- Initial