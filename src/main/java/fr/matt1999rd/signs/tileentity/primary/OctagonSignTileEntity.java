package fr.matt1999rd.signs.tileentity.primary;

import fr.matt1999rd.signs.enums.Form;
import fr.matt1999rd.signs.tileentity.EditingSignTileEntity;
import fr.matt1999rd.signs.tileentity.TEType;


public class OctagonSignTileEntity extends EditingSignTileEntity {

    public OctagonSignTileEntity() {
        super(TEType.OCTOGONE_SIGN);
    }


    @Override
    protected Form getForm() {
        return Form.OCTAGON;
    }
}
