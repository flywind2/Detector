package jp.ac.aiit.Detector.data.db.model;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class ModelTest extends ModelBase{

    public ModelTest() {
        super();
    }

    public int id;
    public String name;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    @Test
    public void createAndSelect() throws Exception {

        ModelTest test = new ModelTest();
        test.createTable();

        test.setId(1);
        test.setName("test");
        test.insert();

        List<ModelTest> ret = test.select("where id=? and name=?", new Object[]{1, "test"});
        for (ModelTest r : ret) {
            assertEquals(1, r.getId());
            assertEquals("test", r.getName());
        }

    }

    @Test
    public void updateAndSelect() throws Exception{

        ModelTest test = new ModelTest();

        test.setId(2);
        test.setName("test2");
        test.update("where id=?", new Object[]{1});
        List<ModelTest> ret1 = test.select( "", null);
        for(ModelTest r: ret1) {
            assertEquals(2, r.getId());
            assertEquals("test2", r.getName());
        }

        test.delete("", null);
        List<ModelTest> ret2 = select( "", null);
        assertEquals(0, ret2.size());

    }

}
