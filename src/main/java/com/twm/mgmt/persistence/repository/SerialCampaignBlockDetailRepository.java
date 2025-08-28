package com.twm.mgmt.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.twm.mgmt.persistence.entity.SerialCampaignBlockDetail;
import com.twm.mgmt.persistence.entity.pk.SerialCampaignBlockDetailId;

@Transactional
@Repository
public interface SerialCampaignBlockDetailRepository extends JpaRepository<SerialCampaignBlockDetail, SerialCampaignBlockDetailId>{
	
	@Query(value = "SELECT COUNT(scbd.ROW_ID) "
			+ "FROM MOMOAPI.SERIAL_CAMPAIGN_BLOCK_MAIN scbm, MOMOAPI.SERIAL_CAMPAIGN_BLOCK_DETAIL scbd "
			+ "WHERE scbm.BLOCK_ID = scbd.BLOCK_ID AND scbm.BLOCK_ID = :blockId ",
			nativeQuery = true)
	public int countSerialBlockDetail(@Param("blockId") Integer blockId);
	
	@Query(value = "SELECT B.* "
			+ " FROM (  "
			+ "    SELECT ROWNUM AS RNUM, A.* "
			+ "       FROM ( "
			+ "				SELECT scbd.ROW_ID, scbd.BLOCK_ID, scbd.SUBID, scbd.PROJECT_CODE, scbd.PROJECT_NAME, scbd.APPLY_DATE, scbd.MEMO, scbd.CREATE_DATE "
			+ "				FROM MOMOAPI.SERIAL_CAMPAIGN_BLOCK_MAIN scbm, MOMOAPI.SERIAL_CAMPAIGN_BLOCK_DETAIL scbd "
			+ "				WHERE scbm.BLOCK_ID = scbd.BLOCK_ID AND scbm.BLOCK_ID = :blockId "
			+ "				ORDER BY scbd.ROW_ID desc "
			+ "        ) A "
			+ "       WHERE ROWNUM BETWEEN 0 AND :offset + :maxSize "
			+ "       ) B "
			+ "    WHERE RNUM > :offset ",
			nativeQuery = true)
	public List<SerialCampaignBlockDetail> getSerialBlockDetail(@Param("blockId") Integer blockId, @Param("offset") Integer offset, @Param("maxSize") Integer maxSize);

	@Query(value = "SELECT scbd.ROW_ID, scbd.BLOCK_ID, scbd.SUBID, scbd.PROJECT_CODE, scbd.PROJECT_NAME, scbd.APPLY_DATE, scbd.MEMO, scbd.CREATE_DATE "
			+ "FROM MOMOAPI.SERIAL_CAMPAIGN_BLOCK_MAIN scbm, MOMOAPI.SERIAL_CAMPAIGN_BLOCK_DETAIL scbd "
			+ "WHERE scbm.BLOCK_ID = scbd.BLOCK_ID AND scbm.BLOCK_ID = :blockId AND scbd.SUBID = :subId "
			+ "ORDER BY scbd.ROW_ID desc ",
			nativeQuery = true)
	public List<SerialCampaignBlockDetail> getSerialBlockDetailBySubId(Integer blockId, String subId);

	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query("DELETE FROM SerialCampaignBlockDetail e WHERE e.rowId = ?1")
	public void deleteByPrimaryKey(int rowId);

	@Query(value = "SELECT * "
			+ "FROM MOMOAPI.SERIAL_CAMPAIGN_BLOCK_DETAIL scbd "
			+ "WHERE scbd.ROW_ID = :rowId ",
			nativeQuery = true)
	public SerialCampaignBlockDetail getSerialBlockDetailByRowId(int rowId);
}
