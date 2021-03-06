package model;

public abstract class QuestCard extends Card{

    protected byte stages;
    protected String[] specialFoes;

    public QuestCard(String _name, byte _stages, String[] _specialFoes, Integer _id){
        name = _name;
        stages = _stages;
        specialFoes = _specialFoes;
        id = _id;
    }

    protected void setStages(byte _stages){
        stages = _stages;
    }

    protected void setSpecialFoe(String[] _foes){
        specialFoes = _foes;
    }

    public byte getStages(){
        return stages;
    }

    public String[] getSpecialFoes(){
        return specialFoes;
    }
}
