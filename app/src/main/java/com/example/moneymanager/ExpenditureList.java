package com.example.moneymanager;

import java.util.ArrayList;

public class ExpenditureList {
    private ArrayList<Expenditure> expenditures;

    ExpenditureList() {
        expenditures = new ArrayList<>();
        expenditures.add(new Expenditure("Exp01","Ăn uống", false));
        expenditures.add(new Expenditure("Exp02", "Sinh hoạt", false));
        expenditures.add(new Expenditure("Exp03", "Đi lại", false));
        expenditures.add(new Expenditure("Exp04", "Sức khỏe", false));
        expenditures.add(new Expenditure("Exp05", "Đám tiệc", false));
        expenditures.add(new Expenditure("Exp06", "Chi khác", false));
        expenditures.add(new Expenditure("Exp07", "Tiền thưởng", true));
        expenditures.add(new Expenditure("Exp08", "Tiền lãi", true));
        expenditures.add(new Expenditure("Exp09", "Tiền lương", true));
        expenditures.add(new Expenditure("Exp10", "Được tặng", true));
        expenditures.add(new Expenditure("Exp11", "Thu khác", true));
    }

    public String getIcon(String expenditureId) {
        if(expenditureId.equals("Exp01")) {
            return "ic_category_food";
        }
        if(expenditureId.equals("Exp02")) {
            return "ic_category_bill";
        }
        if(expenditureId.equals("Exp03")) {
            return "ic_category_travel";
        }
        if(expenditureId.equals("Exp04")) {
            return "ic_category_health";
        }
        if(expenditureId.equals("Exp05")) {
            return "ic_category_party";
        }
        if(expenditureId.equals("Exp06")) {
            return "ic_category_more";
        }
        if(expenditureId.equals("Exp07")) {
            return "ic_category_bonus";
        }
        if(expenditureId.equals("Exp08")) {
            return "ic_category_profit";
        }
        if(expenditureId.equals("Exp09")) {
            return "ic_category_salary";
        }
        if(expenditureId.equals("Exp10")) {
            return "ic_category_gift";
        }
        return "ic_category_more";
    }

    public ArrayList<Expenditure> getList() {
        return this.expenditures;
    }
}
