/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package net.sf.l2j.gameserver.serverpackets;


/**
 * This class ...
 * 
 * @version $Revision: 1.4.2.1.2.3 $ $Date: 2005/03/27 15:29:39 $
 */
public class CharCreateFail extends ServerBasePacket
{
	private static final String _S__26_CHARCREATEFAIL = "[S] 1a CharCreateFail";
	
	public static int REASON_CREATION_FAILED = 0x00;
	public static int REASON_TOO_MANY_CHARACTERS = 0x01;
	public static int REASON_NAME_ALREADY_EXISTS = 0x02;
	public static int REASON_16_ENG_CHARS = 0x03;
	
	private int _error;

	public CharCreateFail(int errorCode)
	{
		_error = errorCode;
	}	
	
	final void runImpl()
	{
		// no long-running tasks
	}
	
	final void writeImpl()
	{
		writeC(0x1a);
		writeD(_error);
	}
	
	/* (non-Javadoc)
	 * @see net.sf.l2j.gameserver.serverpackets.ServerBasePacket#getType()
	 */
	public String getType()
	{
		return _S__26_CHARCREATEFAIL;
	}
	
}
