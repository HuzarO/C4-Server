package net.sf.l2j.gameserver.datatables.sql;

import net.sf.l2j.L2DatabaseFactory;
import net.sf.l2j.gameserver.idfactory.IdFactory;
import net.sf.l2j.gameserver.model.L2Clan;
import net.sf.l2j.gameserver.model.L2ClanMember;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.serverpackets.PledgeShowMemberListAll;
import net.sf.l2j.gameserver.serverpackets.UserInfo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClanTable {
    private static final Logger _log = Logger.getLogger(ClanTable.class.getName());
    private final Map<Integer, L2Clan> _clans;

    private static final String SELECT_CLANS = "SELECT `id`, `name`, `level`, `reputation`, `alliance_id`, `alliance_name`, `leader_id`, `crest_id`, `crest_large_id`, `alliance_crest_id`, `alliance_penalty_expiry_time`, `character_penalty_expiry_time`, `notice`, `notice_enabled`, `introduction` FROM `clans`";

    public static final ClanTable getInstance() {
        return SingletonHolder._instance;
    }

    public ClanTable() {
        _clans = new HashMap<>();

        try (Connection conn = L2DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = conn.prepareStatement(SELECT_CLANS);
             ResultSet rSet = statement.executeQuery()) {
            while (rSet.next()) {
                int id = rSet.getInt("id");

                L2Clan clan = new L2Clan(clanId, rSet.getInt("leader_id"));
                _clans.put(clanId, clan);

                clan.setName(rSet.getString("clan_name"));
                clan.setLevel(rSet.getInt("clan_level"));
                clan.setHasCastle(rSet.getInt("hasCastle"));
                clan.setHasHideout(rSet.getInt("hasHideout"));
                clan.setAllyId(rSet.getInt("ally_id"));
                clan.setAllyName(rSet.getString("ally_name"));

                clan.setCrestId(rSet.getInt("crest_id"));
                clan.setCrestLargeId(rSet.getInt("crest_large_id"));
                clan.setAllyCrestId(rSet.getInt("ally_crest_id"));

            }
        } catch (Exception e) {
            _log.log(Level.SEVERE, "ClanTable: Error restoring ClanTable: ", e);
        }

        _log.info("ClanTable: Restored " + _clans.size() + " clans.");
    }

    public L2Clan getClan(int clanId) {
        return _clans.get(clanId);
    }

    public L2Clan getClanByName(String clanName) {
        for (L2Clan clan : _clans.values()) {
            if (clan.getName().equalsIgnoreCase(clanName)) {
                return clan;
            }
        }

        return null;
    }

    public L2Clan createClan(L2PcInstance player, String clanName) {
        if (player == null) {
            return null;
        }

        if (player.getLevel() < 10) {
            //TODO: player.sendPacket(SystemMessageId.YOU_DO_NOT_MEET_CRITERIA_IN_ORDER_TO_CREATE_A_CLAN
            return null;
        }

        if (player.getClanId() != 0) {
            //TODO: player.sendPacket(SystemMessageId.FAILED_TO_CREATE_CLAN
            return null;
        }

//        if (System.currentTimeMillis() < player.getClanCreateExpiryTime())
//        {
//            //TODO: player.sendPacket(SystemMessageId.YOU_MUST_WAIT_XX_DAYS_BEFORE_CREATING_A_NEW_CLAN);
//            return null;
//        }

//        if (!StringUtil.isAlphaNumeric(clanName))
//        {
//            player.sendPacket(SystemMessageId.CLAN_NAME_INVALID);
//            return null;
//        }

        if (clanName.length() < 2 || clanName.length() > 16) {
            //TODO: player.sendPacket(SystemMessageId.CLAN_NAME_LENGTH_INCORRECT);
            return null;
        }

        if (getClanByName(clanName) != null) { // Clan Name is already taken
            //TODO: player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_ALREADY_EXISTS).addString(clanName));
            return null;
        }

        L2Clan clan = new L2Clan(IdFactory.getInstance().getNextId(), clanName);
        L2ClanMember leader = new L2ClanMember(clan, player);
        clan.setLeader(leader);
        leader.setPlayerInstance(player);
        clan.store();
        player.setClan(clan);
        //player.setPledgeClass(L2ClanMember.calculatePledgeClass(player));
        player.setClanPrivileges(L2Clan.CP_ALL);

        _clans.put(clan.getClanId(), clan);

        player.sendPacket(new PledgeShowMemberListAll(clan, player));
        player.sendPacket(new UserInfo(player));
        //player.sendPacket(SystemMessageId.CLAN_CREATED);
        return clan;
    }

//    public synchronized void destroyClan(int clanId) {
//        L2Clan clan = _clans.get(clanId);
//        if(clan == null) {
//            return;
//        }
//
//        //TODO: clan.broadcastToOnlineMembers(SystemMessage.getSystemMessage(SystemMessageId.CLAN_HAS_DISPERSED));
//
//        final int castleId = clan.getHasCastle();
//        if(castleId == 0) {
//            for(Siege siege : SiegeManager.getInstance().getSieges()) {
//                siege.removeSiegeClan(clan);
//            }
//        }
//
//        // Drop all items from Clan WH
//        clan.getWarehouse().destroyAllItems("ClanRemove", (clan.getLeader() == null ? null : clan.getLeader().getPlayerInstance()), null);
//
//        for(L2ClanMember member : clan.getMembers()) {
//            clan.removeClanMember(member.getObjectId(), 0);
//        }
//    }

    private static final class SingletonHolder {
        protected static final ClanTable _instance = new ClanTable();
    }
}
