package com.example.utils;

import java.io.Serializable;

import android.os.Parcel;
import android.os.Parcelable;

public class ContacterObj implements Parcelable {
	Contacter[] contacts;

	public Contacter[] getContacts() {
		return contacts;
	}

	public class Contacter {
		String contacts;
		String id;
		String name;
		String email;
		String address;
		String gender;

		public PhoneInContacter getPhone() {
			return phone;
		}

		public void setPhone(PhoneInContacter phone) {
			this.phone = phone;
		}

		PhoneInContacter phone;

		public String getContacts() {
			return contacts;
		}

		public void setContacts(String contacts) {
			this.contacts = contacts;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getEmail() {
			return email;
		}

		public void setEmail(String email) {
			this.email = email;
		}

		public String getAddress() {
			return address;
		}

		public void setAddress(String address) {
			this.address = address;
		}

		public String getGender() {
			return gender;
		}

		public void setGender(String gender) {
			this.gender = gender;
		}

		public class PhoneInContacter {
			String mobile;
			String home;
			String office;

			public String getMobile() {
				return mobile;
			}

			public void setMobile(String mobile) {
				this.mobile = mobile;
			}

			public String getHome() {
				return home;
			}

			public void setHome(String home) {
				this.home = home;
			}

			public String getOffice() {
				return office;
			}

			public void setOffice(String office) {
				this.office = office;
			}
		}
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		//dest.writeString();
		
	}
	
	public static final Parcelable.Creator<ContacterObj> CREATOR=new Creator<ContacterObj>() {
		
		@Override
		public ContacterObj[] newArray(int size) {
			// TODO Auto-generated method stub
			
			return null;
		}
		
		@Override
		public ContacterObj createFromParcel(Parcel source) {
			// TODO Auto-generated method stub
			
			return null;
		}
	};
	
}
