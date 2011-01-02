/*
 * Copyright 2010 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.soebes.maven.plugins.mlv.licenses;

import java.util.ArrayList;
import java.util.List;

import org.apache.maven.model.License;

import com.soebes.maven.plugins.mlv.model.LicenseItem;
import com.soebes.maven.plugins.mlv.model.LicensesContainer;
import com.soebes.maven.plugins.mlv.model.LicensesList;


/**
 * This class offers you methods to categorize
 * licenses into the different categories. 
 * 
 * @author Karl Heinz Marbaise
 */
public class LicenseValidator {

	/**
	 * Where the licenses are stored.
	 */
	private final LicensesContainer licensesContainer;

	private boolean strictChecking;

	public LicenseValidator(LicensesContainer licensesContainer) {
		strictChecking = false;
		this.licensesContainer = licensesContainer;
	}

	/**
	 * This will return the valid licenses or an empty
	 * List if no such entry in the license.xml file exist.
	 * @return The list of LicenseItem
	 */
	public List<LicenseItem> getValid() {
		if (licensesContainer.getValid() == null) {
			return new ArrayList<LicenseItem> ();
		} else {
			return licensesContainer.getValid().getLicenses();
		}
	}

	public void setValid(List<LicenseItem> valid) {
		this.licensesContainer.getInvalid().setLicenses(valid);
	}

	/**
	 * This will return the Invalid license list from the 
	 * license.xml file or an empty list if no such area
	 * is defined in the license.xml file.
	 * @return The List of Invalid licenses.
	 */
	public List<LicenseItem> getInvalid() {
		if (licensesContainer.getInvalid() == null) {
			return new ArrayList<LicenseItem>();
		} else {
			return licensesContainer.getInvalid().getLicenses();
		}
	}

	public void setInvalid(ArrayList<LicenseItem> invalid) {
		this.licensesContainer.getInvalid().setLicenses(invalid);
	}

	/**
	 * This will return the list of licenses which
	 * are defined in the warning area of the license.xml 
	 * file or an empty list if no such area has been
	 * defined in the license.xml file.
	 * @return The list of Warning licenses.
	 */
	public List<LicenseItem> getWarning() {
		if (licensesContainer.getWarning() == null) {
			return new ArrayList<LicenseItem>();
		} else {
			return licensesContainer.getWarning().getLicenses();
		}
	}
	
	public void setWarning(ArrayList<LicenseItem> warning) {
		this.licensesContainer.getWarning().setLicenses(warning);
	}

	/**
	 * This method will check if the given License name and URL
	 * is existing in the given CheckLicense class which contains
	 * a single License which can be described with multiple names
	 * and multiple URL's. 
	 * @param item The Instance of the CheckLicense class.
	 * @param cl The instance of the License to check
	 * @return true if the given license URL and Name are 
	 *   
	 */
	private boolean checkNamesAndURLs(LicenseItem item, License cl) {
		boolean result = false;
		if (checkNames(item,cl) && checkUrls(item, cl)) {
			result = true;
		}
		return result;
	}

	/**
	 * This method will check if the given License name or url
	 * is existing in the given CheckLicense class which contains
	 * a single License which can be described with multiple names
	 * and multiple URL's. 
	 * This will
	 * @param item The Instance of the CheckLicense class.
	 * @param cl The instance of the License to check
	 * @return true if the given license URL and Name are 
	 */
	private boolean checkNamesOrURLs(LicenseItem item, License cl) {
		boolean result = false;
		if (checkNames(item, cl)) {
			result = true;
		}
		if (checkUrls(item, cl)) {
			result = true;
		}
		return result;
	}

	/**
	 * This will check the given License against the given list of 
	 * licenses.
	 * @param checkList The list of licenses which will be used to check against.
	 * @param cl The license which will be check to be part of the above list.
	 * @return true if the license has an equal name and/or equal URL false otherwise.
	 */
	private boolean check(List<LicenseItem> checkList, License cl) {
		boolean result = false;
		for (LicenseItem item : checkList) {
			if (isStrictChecking()) {
				if (checkNamesAndURLs(item, cl)) {
					result = true;
				}
			} else {
				if (checkNamesOrURLs(item, cl)) {
					result = true;
				}
			}
		}
		return result;
	}

	private String checkId(List<LicenseItem> checkList, License cl) {
		String result = null;
		for (LicenseItem item : checkList) {
			if (isStrictChecking()) {
				if (checkNamesAndURLs(item, cl)) {
					result = item.getId();
				}
			} else {
				if (checkNamesOrURLs(item, cl)) {
					result = item.getId();
				}
			}
		}
		return result;
	}

	/**
	 * This will check if the given license is 
	 * part of the <code>valid</code> licenses or not.
	 * 
	 * @param cl License which will be checked.
	 * @return true if License is part of the valid licenses category,
	 * 		false otherwise.
	 */
	public boolean isValid(License cl) {
		if (check(getValid(), cl)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * This will return the Id of the License.
	 * @param cl
	 * @return null if not found or the id of the found license.
	 */
	public String getValidId(License cl) {
		return checkId(getValid(), cl);
	}

	/**
	 * The given list of licenses has to be part of the 
	 * <code>valid</code> section of the configuration
	 * file which means in other word an conjunction (logical and).
	 * @param licenses
	 * @return
	 */
	public boolean isValid(ArrayList<License> licenses) {
		boolean result = true;
		if (licenses.isEmpty()) {
			result = false;
		} else {
			for (License license : licenses) {
				if (!isValid(license)) {
					result = false;
				}
			}
		}
		return result;
	}

	/**
	 * This method will get the Id's from the valid area
	 * of the licenses.xml file. 
//FIXME: More docs. analyze..
	 * @param licenses
	 * @return
	 */
	public ArrayList<String> getValidIds(ArrayList<License> licenses) {
		ArrayList<String> result = new ArrayList<String>();
		if (licenses.isEmpty()) {
			return result;
		} else {
			for (License license : licenses) {
				String t = getValidId(license);
				if (t != null) {
					if (!result.contains(t)) {
						result.add(t);
					}
				}
			}
		}
		return result;
	}
	public ArrayList<String> getInvalidIds(ArrayList<License> licenses) {
		ArrayList<String> result = new ArrayList<String>();
		if (licenses.isEmpty()) {
			return result;
		} else {
			for (License license : licenses) {
				String t = getInvalidId(license);
				if (t != null) {
					if (!result.contains(t)) {
						result.add(t);
					}
				}
			}
		}
		return result;
	}
	public ArrayList<String> getWarningIds(ArrayList<License> licenses) {
		ArrayList<String> result = new ArrayList<String>();
		if (licenses.isEmpty()) {
			return result;
		} else {
			for (License license : licenses) {
				String t = getWarningId(license);
				if (t != null) {
					if (!result.contains(t)) {
						result.add(t);
					}
				}
			}
		}
		return result;
	}

	/**
	 * The given list of licenses has to be part of the 
	 * <code>invalid</code> section of the configuration
	 * file which means in other word an conjunction (logical and).
	 * @param licenses
	 * @return
	 */
	public boolean isInvalid(ArrayList<License> licenses) {
		boolean result = true;
		if (licenses.isEmpty()) {
			result = false;
		} else {
			for (License license : licenses) {
				if (!isInvalid(license)) {
					result = false;
				}
			}
		}
		return result;
	}

	/**
	 * The given list of licenses has to be part of the 
	 * <code>warning</code> section of the configuration
	 * file which means in other word an conjunction (logical and).
	 * @param licenses
	 * @return
	 */
	public boolean isWarning(ArrayList<License> licenses) {
		boolean result = true;
		if (licenses.isEmpty()) {
			result = false;
		} else {
			for (License license : licenses) {
				if (!isWarning(license)) {
					result = false;
				}
			}
		}
		return result;
	}

	/**
	 * TBD: Enhance description
	 * 
	 * A list of licenses will be categorized as <code>Unknown</code>
	 * if one or more licenses can be categorized into different categories.
	 * 
	 * For example if you have a list with three licenses
	 * 
	 *       isValid  isWarning isInvalid  isUnknown
	 * A       X
	 * B       X
	 * C                X
	 * 
	 * @param licenses The list with the licenses which will be checked.
	 * @return true if a license is from no category.
	 * 
	 */
	public boolean isUnknown(ArrayList<License> licenses) {
		boolean result = false;
		if (licenses.isEmpty()) {
			result = true;
		} else {
			int changeValid = 0;
			int changeInvalid = 0;
			int changeWarning = 0;
			for (License license : licenses) {
				if (isValid(license)) {
					changeValid ++;
				}
				if (isInvalid(license)) {
					changeInvalid ++;
				}
				if (isWarning(license)) {
					changeWarning++;
				}
			}

			if (changeValid == licenses.size()) {
				result = false;
			} else if (changeInvalid == licenses.size()) {
				result = false;
			} else if (changeWarning == licenses.size()) {
				result = false;
			} else {
				result = true;
			}
		}
		return result;
	}

	/**
	 * Check if a given license is not part of 
	 * one the categories <b>invalid</b>, 
	 * <b>valid</b> or <b>warning</b>.
	 * @param license
	 * @return true not part of any category false otherwise.
	 */
	public boolean isUnknown(License license) {
		boolean result = true;
		if (isValid(license)) {
			result = false;
		} else if (isInvalid(license)) {
			result = false;
		} else if (isWarning(license)) {
			result = false;
		}

		return result;
	}

	/**
	 * This will check if the given license is 
	 * part of the <code>invalid</code> category or not.
	 * 
	 * @param cl License which will be checked.
	 * @return true if License part of the invalid licenses false otherwise.
	 */
	public boolean isInvalid(License cl) {
		return check(getInvalid(), cl);
	}

	public String getInvalidId(License cl) {
		return checkId(getInvalid(), cl);
	}

	/**
	 * This will check if the given license is 
	 * part of the <code>warning</code> category or not.
	 * 
	 * @param cl License which will be checked.
	 * @return true if License part of the warning licenses false otherwise.
	 */
	public boolean isWarning(License cl) {
		return check(getWarning(), cl);
	}

	public String getWarningId(License cl) {
		return checkId(getWarning(), cl);
	}

	/**
	 * Check the given License URL against the CheckLicense which 
	 * can contain more than one.
	 * @param item The CheckLicense instance which is used to check.
	 * @param cl The license which will be checked
	 * @return true if the License <b>URL</b> is equal to one of the URLs in 
	 *   CheckLicense.
	 */
	private boolean checkUrls(LicenseItem item, License cl) {
		boolean result = false;
		for (String checkUrl : item.getUrls()) {
			if (checkUrl.equals(cl.getUrl())) {
				result = true;
			}
		}
		return result;
	}

	/**
	 * Check the given License Name against the CheckLicense which 
	 * can contain more than one.
	 * @param item The CheckLicense instance which is used to check.
	 * @param cl The license which will be checked
	 * @return true if the License <b>name</b> is equal to one of the Names in 
	 *   CheckLicense.
	 */
	private boolean checkNames(LicenseItem item, License cl) {
		boolean result = false;
		for (String checkName : item.getNames()) {
			if (checkName.equals(cl.getName())) {
				result = true;
			}
		}
		return result;
	}

	public void setStrictChecking(boolean strictChecking) {
		this.strictChecking = strictChecking;
	}

	public boolean isStrictChecking() {
		return strictChecking;
	}
}
