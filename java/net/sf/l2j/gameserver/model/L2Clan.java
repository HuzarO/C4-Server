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
package net.sf.l2j.gameserver.model;

import javolution.util.FastList;
import net.sf.l2j.L2DatabaseFactory;
import net.sf.l2j.gameserver.communitybbs.BB.Forum;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class ...
 *
 * @version $Revision: 1.7.2.4.2.7 $ $Date: 2005/04/06 16:13:41 $
 */
public class L2Clan {
    private static final Logger _log = Logger.getLogger(L2Clan.class.getName());

    private final int _id;
    private L2ClanMember _leader;

    private final Map<Integer, L2ClanMember> _members = new HashMap<>();

    private String _name;

    private ItemContainer _warehouse = new ClanWarehouse(this);
    private List<Integer> _atWarWith = new FastList<Integer>();

    private boolean _hasCrestLarge;

    private Forum _Forum;

    //  Clan Privileges
    public static final int CP_NOTHING = 0;
    public static final int CP_CL_JOIN_CLAN = 1; // Join clan
    public static final int CP_CL_GIVE_TITLE = 2; // Give a title
    public static final int CP_CL_VIEW_WAREHOUSE = 4; // View warehouse content
    public static final int CP_CL_REGISTER_CREST = 8; // Register clan crest
    public static final int CP_CH_OPEN_DOOR = 16; // open a door
    public static final int CP_CH_OTHER_RIGHTS = 32; //??
    public static final int CP_CH_DISMISS = 64; //??
    public static final int CP_CS_OPEN_DOOR = 128;
    public static final int CP_CS_OTHER_RIGHTS = 256; //???
    public static final int CP_CS_DISMISS = 512; //???
    public static final int CP_ALL = 1023;

    private static final String GET_MEMBERS = "SELECT * FROM `characters` WHERE `clanid`=?";

    /**
     * TODO: Rewrite this class
     *
     * @param id
     * @param leaderId
     */
    public L2Clan(int id, int leaderId) {
        _id = id;

        try (Connection con = L2DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(GET_MEMBERS)) {
            statement.setInt(1, id);
            ResultSet rSet = statement.executeQuery();
            while (rSet.next()) {
                L2ClanMember member = new L2ClanMember(this, rSet);
                if (member.getObjectId() == leaderId) {
                    setLeader(member);
                } else {
                    _members.put(member.getObjectId(), member);
                }
            }

            rSet.close();
        } catch (Exception e) {
            _log.log(Level.SEVERE, "L2Clan: Error while restoring :", e);
        }
    }

    /**
     * TODO: Rewrite this class
     *
     * @param id
     * @param name
     */
    public L2Clan(int id, String name) {
        _id = id;
        _name = name;
    }

    public void setLeader(L2ClanMember leader) {
        _leader = leader;
    }

    public L2ClanMember getLeader() {
        return _leader;
    }

    public int getLeaderId() {
        return _leader.getObjectId();
    }
}
