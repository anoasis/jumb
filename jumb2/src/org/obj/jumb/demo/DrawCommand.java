// $Id: DrawCommand.java,v 1.6 2006/10/09 11:35:46 belaban Exp $

package org.obj.jumb.demo;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.DataInputStream;
import java.io.Serializable;

/**
 * Encapsulates information about a draw command.
 * Used by the {@link Draw} and other demos.
 *
 */
public class DrawCommand implements Serializable {
    static final byte DRAW=1;
    static final byte CLEAR=2;
    byte mode;
    int x=0;
    int y=0;
    int r=0;
    int g=0;
    int b=0;

    public DrawCommand() { // needed for streamable
    }

    DrawCommand(byte mode) {
        this.mode=mode;
    }

    DrawCommand(byte mode, int x, int y, int r, int g, int b) {
        this.mode=mode;
        this.x=x;
        this.y=y;
        this.r=r;
        this.g=g;
        this.b=b;
    }


    public void writeTo(DataOutputStream out) throws IOException {
        out.writeByte(mode);
        out.writeInt(x);
        out.writeInt(y);
        out.writeInt(r);
        out.writeInt(g);
        out.writeInt(b);
    }

    public void readFrom(DataInputStream in) throws IOException, IllegalAccessException, InstantiationException {
        mode=in.readByte();
        x=in.readInt();
        y=in.readInt();
        r=in.readInt();
        g=in.readInt();
        b=in.readInt();
    }


    public String toString() {
        StringBuffer ret=new StringBuffer();
        switch(mode) {
            case DRAW: ret.append("DRAW(" + x + ", " + y + ") [" + r + '|' + g + '|' + b + ']');
                break;
            case CLEAR: ret.append("CLEAR");
                break;
            default:
                return "<undefined>";
        }
        return ret.toString();
    }

}
